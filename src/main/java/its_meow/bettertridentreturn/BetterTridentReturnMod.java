package its_meow.bettertridentreturn;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod.EventBusSubscriber(modid = BetterTridentReturnMod.MOD_ID)
@Mod(value = BetterTridentReturnMod.MOD_ID)
public class BetterTridentReturnMod {

    public static final String MOD_ID = "bettertridentreturn";
    private static final int OFFHAND_SLOT = -1;
    private static final int NOT_FOUND_SLOT = -2;
    private static final int SEARCH_ID_SLOT = -3;

    public BetterTridentReturnMod() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @SubscribeEvent
    public static void onItemThrown(LivingEntityUseItemEvent.Stop event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(event.getItem().getItem() instanceof TridentItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, event.getItem()) > 0) {
                int i = 72000 - event.getDuration();
                if (i >= 10) {
                    int j = EnchantmentHelper.getRiptide(event.getItem());
                    if (j <= 0 || player.isInWaterOrRain()) {
                        ItemStack newStack = event.getItem();
                        if(newStack.getTag() == null) {
                            newStack.setTag(new CompoundTag());
                        }
                        newStack.getTag().putInt("slot_thrown_from", SEARCH_ID_SLOT); // unique identifier
                        int slot = getSlotFor(player.getInventory(), event.getItem());
                        newStack.getTag().putInt("slot_thrown_from", slot);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerTickEvent event) {
        Player player = event.player;
        for(ItemStack stack : player.getInventory().items) {
            checkStack(player, stack);
        }
    }

    public static void checkStack(Player player, ItemStack stack) {
        if(stack.getItem() instanceof TridentItem && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, stack) > 0) {
            if(stack.getTag() != null) {
                if(stack.getTag().contains("slot_thrown_from", NBT.TAG_INT)) {
                    int slot = stack.getTag().getInt("slot_thrown_from");
                    int curSlot = getSlotFor(player.getInventory(), stack);
                    if(slot != curSlot) {
                        if(slot == OFFHAND_SLOT) {
                            ItemStack fromSlot = player.getOffhandItem();
                            if(fromSlot == null || fromSlot.isEmpty()) {
                                player.getInventory().removeItemNoUpdate(curSlot);
                                stack.getTag().remove("slot_thrown_from");
                                player.getInventory().offhand.set(0, stack);
                            }
                        } else if(slot != NOT_FOUND_SLOT) {
                            ItemStack fromSlot = player.getInventory().getItem(slot);
                            if(fromSlot == null || fromSlot.isEmpty()) {
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
        return stack1.getItem() == stack2.getItem() && ItemStack.tagMatches(stack1, stack2);
    }

}
