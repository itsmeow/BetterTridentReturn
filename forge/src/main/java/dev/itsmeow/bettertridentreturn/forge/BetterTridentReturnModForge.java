package dev.itsmeow.bettertridentreturn.forge;

import dev.itsmeow.bettertridentreturn.BetterTridentReturnMod;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;

@Mod(BetterTridentReturnMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BetterTridentReturnMod.MOD_ID)
public class BetterTridentReturnModForge {

    public BetterTridentReturnModForge() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (s, b) -> true));
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player)
            BetterTridentReturnMod.onItemUseFinish((Player) event.getEntity(), event.getItem(), event.getDuration());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        BetterTridentReturnMod.onPlayerTick(event.player);
    }
}
