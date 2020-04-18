package its_meow.bettertridentreturn;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterTridentReturnMod.MOD_ID)
@Mod(value = BetterTridentReturnMod.MOD_ID)
public class BetterTridentReturnMod {

    public static final String MOD_ID = "bettertridentreturn";

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
                        newStack.getTag().putInt("slot_thrown_from", -3); // unique identifier
                        int slot = getSlotFor(player.inventory, event.getItem());
                        newStack.getTag().putInt("slot_thrown_from", slot);
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
                    int curSlot = getSlotFor(player.inventory, stack);
                    if(slot != curSlot) {
                        if(slot == -1) {
                            ItemStack fromSlot = player.getHeldItemOffhand();
                            if(fromSlot == null || fromSlot.isEmpty()) {
                                player.inventory.removeStackFromSlot(curSlot);
                                stack.getTag().remove("slot_thrown_from");
                                player.inventory.offHandInventory.set(0, stack);
                            }
                        } else if(slot != -2) {
                            ItemStack fromSlot = player.inventory.getStackInSlot(slot);
                            if(fromSlot == null || fromSlot.isEmpty()) {
                                player.inventory.removeStackFromSlot(curSlot);
                                stack.getTag().remove("slot_thrown_from");
                                player.inventory.setInventorySlotContents(slot, stack);
                            }
                        }
                    }
                }
            }
        }
    }

    public static int getSlotFor(PlayerInventory inv, ItemStack stack) {
        for(int i = 0; i < inv.mainInventory.size(); ++i) {
            if (!inv.mainInventory.get(i).isEmpty() && stackEqualExact(stack, inv.mainInventory.get(i))) {
                return i;
            }
        }
        if(!inv.offHandInventory.get(0).isEmpty() && stackEqualExact(stack, inv.offHandInventory.get(0))) {
            return -1;
        }


        return -2;
    }

    private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

}
