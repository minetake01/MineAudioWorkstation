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

    @ModifyConstant(
        method = "getAdjustedPitch",
        constant = @Constant(floatValue = 2.0f)
    )
    private static float modifyMaxPitch(float original) {
        return 8.0f;
    }
}
