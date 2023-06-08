package net.minetake.mineaudioworkstation.mixin;

import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Properties.class)
public class NotePropertyMixin {
    // NOTE定数の最大値を24から48に変更する
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(intValue = 24),
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=note"
            )
        )
    )
    private static int modifyNoteMaxValue(int original) {
        return 48;
    }
}
