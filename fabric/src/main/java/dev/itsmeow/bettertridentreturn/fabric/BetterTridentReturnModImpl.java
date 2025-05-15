package dev.itsmeow.bettertridentreturn.fabric;

import dev.itsmeow.bettertridentreturn.BetterTridentReturnModFabric;
import net.minecraft.core.component.DataComponentType;

public class BetterTridentReturnModImpl {
    public static DataComponentType<Integer> getTridentReturnDataType() {
        return BetterTridentReturnModFabric.TRIDENT_RETURN_SLOT_DATA;
    }
}
