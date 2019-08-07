package its_meow.bettertridentreturn;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterTridentReturnMod.MOD_ID)//, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(value = BetterTridentReturnMod.MOD_ID)
public class BetterTridentReturnMod {

    public static final String MOD_ID = "bettertridentreturn";
    public static final String VERSION = "@VERSION@";

    @SubscribeEvent
    public static void onItemThrown(LivingEntityUseItemEvent.Stop event) {
        if(event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if(event.getItem().getItem() instanceof TridentItem && EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, event.getItem()) > 0) {
                int i = 72000 - event.getDuration();
                if (i >= 10) {
                    int j = EnchantmentHelper.getRiptideModifier(event.getItem());
                    if (j <= 0 || player.isWet()) {
                        ItemStack newStack = event.getItem();
                        if(newStack.getTag() == null) {
                            newStack.setTag(new CompoundNBT());
                        }
                        int slot = player.inventory.getSlotFor(event.getItem());
                        newStack.getTag().putInt("slot_thrown_from", slot);
                        System.out.println(slot);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerTickEvent event) {
        PlayerEntity player = event.player;
        for(ItemStack stack : player.inventory.mainInventory) {
            checkStack(player, stack);
        }
    }

    public static void checkStack(PlayerEntity player, ItemStack stack) {
        if(stack.getItem() instanceof TridentItem && EnchantmentHelper.getEnchantmentLevel(Enchantments.LOYALTY, stack) > 0) {
            if(stack.getTag() != null) {
                if(stack.getTag().contains("slot_thrown_from", NBT.TAG_INT)) {
                    int slot = stack.getTag().getInt("slot_thrown_from");
                    stack.getTag().remove("slot_thrown_from");
                    int curSlot = player.inventory.getSlotFor(stack);
                    if(slot != curSlot) {
                        if(slot == -1) {
                            System.out.println(slot);
                            ItemStack fromSlot = player.getHeldItemOffhand();
                            if(fromSlot == null || fromSlot.isEmpty()) {
                                player.inventory.removeStackFromSlot(curSlot);
                                player.inventory.offHandInventory.set(0, stack);
                            }
                        } else {
                            System.out.println(slot);
                            ItemStack fromSlot = player.inventory.getStackInSlot(slot);
                            if(fromSlot == null || fromSlot.isEmpty()) {
                                player.inventory.removeStackFromSlot(curSlot);
                                player.inventory.setInventorySlotContents(slot, stack);
                            }
                        }
                    }
                }
            }
        }
    }

}