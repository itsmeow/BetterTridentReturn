package dev.itsmeow.bettertridentreturn.neoforge;

import com.mojang.serialization.Codec;
import dev.itsmeow.bettertridentreturn.BetterTridentReturnMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(BetterTridentReturnMod.MOD_ID)
@EventBusSubscriber(modid = BetterTridentReturnMod.MOD_ID)
public class BetterTridentReturnModNeoForge {

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BetterTridentReturnMod.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> TRIDENT_RETURN_SLOT_DATA = REGISTRAR.registerComponentType(
            "trident_return_slot",
            builder -> builder.persistent(Codec.INT)
    );

    public BetterTridentReturnModNeoForge(IEventBus modEventBus) {
        REGISTRAR.register(modEventBus);
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player)
            BetterTridentReturnMod.onItemUseFinish((Player) event.getEntity(), event.getItem(), event.getDuration());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        BetterTridentReturnMod.onPlayerTick(event.getEntity());
    }
}
