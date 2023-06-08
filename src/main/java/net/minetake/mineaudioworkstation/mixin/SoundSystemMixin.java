package net.minetake.mineaudioworkstation.mixin;

import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Shadow @Final @Mutable
    private static float MAX_PITCH = 8.0f;
    @Shadow @Final @Mutable
    private static float MIN_PITCH = Float.MIN_VALUE;

    @ModifyConstant(
        method = "getAdjustedPitch",
        constant = @Constant(floatValue = 2.0f)
    )
    private static float modifyMaxPitch(float original) {
        return MAX_PITCH;
    }

    @ModifyConstant(
        method = "getAdjustedPitch",
        constant = @Constant(floatValue = 0.5f)
    )
    private static float modifyMinPitch(float original) {
        return MIN_PITCH;
    }
}
