package dev.itsmeow.bettertridentreturn.mixin;

import dev.itsmeow.bettertridentreturn.BetterTridentReturnMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Inject(at = @At("HEAD"), method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Z")
    private void injectReleaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> callback) {
        if(livingEntity instanceof Player player) {
            BetterTridentReturnMod.onItemUseFinish(player, itemStack, i);
        }
    }

}
