package dev.itsmeow.bettertridentreturn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class BetterTridentReturnMod {

    public static final String MOD_ID = "bettertridentreturn";
    private static final int OFFHAND_SLOT = -1;
    private static final int NOT_FOUND_SLOT = -2;

    @ExpectPlatform
    public static DataComponentType<Integer> getTridentReturnDataType() {
        return null;
    }

    public static void onItemUseFinish(Player player, ItemStack itemStack, int duration) {
        if (!(itemStack.getItem() instanceof TridentItem) || player.level().isClientSide()) {
            return;
        }
        if(itemStack.getItem().getUseDuration(itemStack, player) - duration < 10) {
            return;
        }
        if(player.level() instanceof ServerLevel serverLevel) {
            // Check for loyalty enchantment
            if(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, itemStack, player) <= 0) {
                return;
            }
        } else {
            return;
        }

        // If Riptide is executed, don't add the tag
        float f = EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player);
        if (f > 0.0F && player.isInWaterOrRain()) {
            return;
        }

        int slot = getSlotFor(player.getInventory(), itemStack);
        itemStack.set(getTridentReturnDataType(), slot);
    }

    public static void onPlayerTick(Player player) {
        for(ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            checkStack(player, stack);
        }
    }

    public static void checkStack(Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof TridentItem) || player.level().isClientSide()) {
            return;
        }
        if(player.level() instanceof ServerLevel serverLevel) {
            // Check for loyalty enchantment
            if(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, stack, player) <= 0) {
                return;
            }
        } else {
            return;
        }
        if(!stack.has(getTridentReturnDataType())) {
            return;
        }
        int slot = stack.get(getTridentReturnDataType());
        int curSlot = getSlotFor(player.getInventory(), stack);
        if(slot != curSlot) {
            if(slot == OFFHAND_SLOT) {
                ItemStack fromSlot = player.getOffhandItem();
                if(fromSlot.isEmpty()) {
                    player.getInventory().removeItemNoUpdate(curSlot);
                    stack.remove(getTridentReturnDataType());
                    player.getInventory().setItem(Inventory.SLOT_OFFHAND, stack);
                }
            } else if(slot != NOT_FOUND_SLOT) {
                ItemStack fromSlot = player.getInventory().getItem(slot);
                if(fromSlot.isEmpty()) {
                    player.getInventory().removeItemNoUpdate(curSlot);
                    stack.remove(getTridentReturnDataType());
                    player.getInventory().setItem(slot, stack);
                }
            }
        }
    }

    public static int getSlotFor(Inventory inv, ItemStack stack) {
        for(int i = 0; i < inv.getNonEquipmentItems().size(); ++i) {
            if (!inv.getNonEquipmentItems().get(i).isEmpty() && stackEqualExact(stack, inv.getNonEquipmentItems().get(i))) {
                return i;
            }
        }
        if(!inv.getItem(Inventory.SLOT_OFFHAND).isEmpty() && stackEqualExact(stack, inv.getItem(Inventory.SLOT_OFFHAND))) {
            return OFFHAND_SLOT;
        }

        return NOT_FOUND_SLOT;
    }

    private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.matches(stack1, stack2);
    }

}
