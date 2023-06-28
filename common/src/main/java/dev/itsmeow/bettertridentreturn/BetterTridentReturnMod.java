package dev.itsmeow.bettertridentreturn;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    public static void onItemUseFinish(Player player, ItemStack itemStack, int duration) {
        if (itemStack.getItem() instanceof TridentItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, itemStack) > 0 && itemStack.getItem().getUseDuration(itemStack) - duration >= 10) {
            int j = EnchantmentHelper.getRiptide(itemStack);
            if ((j <= 0 || player.isInWaterOrRain()) && !player.level().isClientSide() && j == 0) {
                if (itemStack.getTag() == null) {
                    itemStack.setTag(new CompoundTag());
                }
                int slot = getSlotFor(player.getInventory(), itemStack);
                itemStack.getTag().putInt("slot_thrown_from", slot);
            }
        }
    }

    public static void onPlayerTick(Player player) {
        for(ItemStack stack : player.getInventory().items) {
            checkStack(player, stack);
        }
    }

    public static void checkStack(Player player, ItemStack stack) {
        if(stack.getItem() instanceof TridentItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, stack) > 0) {
            if(stack.getTag() != null) {
                if(stack.getTag().contains("slot_thrown_from", Tag.TAG_INT)) {
                    int slot = stack.getTag().getInt("slot_thrown_from");
                    int curSlot = getSlotFor(player.getInventory(), stack);
                    if(slot != curSlot) {
                        if(slot == OFFHAND_SLOT) {
                            ItemStack fromSlot = player.getOffhandItem();
                            if(fromSlot.isEmpty()) {
                                player.getInventory().removeItemNoUpdate(curSlot);
                                stack.getTag().remove("slot_thrown_from");
                                player.getInventory().offhand.set(0, stack);
                            }
                        } else if(slot != NOT_FOUND_SLOT) {
                            ItemStack fromSlot = player.getInventory().getItem(slot);
                            if(fromSlot.isEmpty()) {
                                player.getInventory().removeItemNoUpdate(curSlot);
                                stack.getTag().remove("slot_thrown_from");
                                player.getInventory().setItem(slot, stack);
                            }
                        }
                    }
                }
            }
        }
    }

    public static int getSlotFor(Inventory inv, ItemStack stack) {
        for(int i = 0; i < inv.items.size(); ++i) {
            if (!inv.items.get(i).isEmpty() && stackEqualExact(stack, inv.items.get(i))) {
                return i;
            }
        }
        if(!inv.offhand.get(0).isEmpty() && stackEqualExact(stack, inv.offhand.get(0))) {
            return OFFHAND_SLOT;
        }


        return NOT_FOUND_SLOT;
    }

    private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.matches(stack1, stack2);
    }

}
