package dev.itsmeow.bettertridentreturn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class BetterTridentReturnModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            server.getPlayerList().getPlayers().forEach(BetterTridentReturnMod::onPlayerTick);
        });
    }

}
