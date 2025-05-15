package dev.itsmeow.bettertridentreturn;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class BetterTridentReturnModFabric implements ModInitializer {

    public static final DataComponentType<Integer> TRIDENT_RETURN_SLOT_DATA = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath(BetterTridentReturnMod.MOD_ID, "trident_return_slot"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT).build()
    );

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(BetterTridentReturnMod::onPlayerTick);
        });
    }

}
