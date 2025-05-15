package dev.itsmeow.bettertridentreturn.neoforge;

import net.minecraft.core.component.DataComponentType;

public class BetterTridentReturnModImpl {

    public static DataComponentType<Integer> getTridentReturnDataType() {
        return BetterTridentReturnModNeoForge.TRIDENT_RETURN_SLOT_DATA.get();
    }

}
