package net.minetake.mineaudioworkstation.mixin;

import net.minecraft.server.MinecraftServer;
import net.minetake.mineaudioworkstation.BPMManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(
        method = "runServer",
        constant = @Constant(longValue = 50L)
    )
    long modifyMSPT(long tickTime) {
        return (long) BPMManager.MSPT;
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void saveMSPT(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        BPMManager.save();
    }
}
