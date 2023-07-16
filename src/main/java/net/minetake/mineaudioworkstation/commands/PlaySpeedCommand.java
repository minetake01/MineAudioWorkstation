package net.minetake.mineaudioworkstation.commands;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.*;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minetake.mineaudioworkstation.BPMManager;

public final class PlaySpeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = literal("playspeed").requires(source -> source.hasPermissionLevel(2));

        builder.then(literal("base")
            .then(argument("bpm", floatArg(1))
                .executes(ctx -> {
                    float bpm = getFloat(ctx, "bpm");
                    BPMManager.setBPM(bpm);
                    ctx.getSource().sendMessage(Text.literal("Set to " + BPMManager.getBPM() + " bpm with repeater delay level " + BPMManager.getDelayLevel()));
                    return Command.SINGLE_SUCCESS;
                })
                .then(argument("delayLevel(Optional)", integer(1))
                    .executes(ctx -> {
                        float bpm = getFloat(ctx, "bpm");
                        int delayLevel = getInteger(ctx, "delayLevel(Optional)");
                        BPMManager.setBPM(bpm, delayLevel);
                        ctx.getSource().sendMessage(Text.literal("Set to " + BPMManager.getBPM() + " bpm with repeater delay level " + BPMManager.getDelayLevel()));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
        );

        builder.then(literal("modify")
            .then(argument("bpm(multiplier)", floatArg(0))
                .executes(ctx -> {
                    float bpm = getFloat(ctx, "bpm(multiplier)");
                    if (bpm == 0) {
                        ctx.getSource().sendError(Text.literal("BPM cannot be 0."));
                        return Command.SINGLE_SUCCESS;
                    }
                    float prevBPM = BPMManager.multiplyBPM(bpm);
                    ctx.getSource().sendMessage(Text.literal("Modified from " + prevBPM + " bpm to " + BPMManager.getBPM() + " bpm with repeater delay level " + BPMManager.getDelayLevel()));
                    return Command.SINGLE_SUCCESS;
                })
                .then(argument("delayLevel(Optional)", integer(1))
                    .executes(ctx -> {
                        float bpm = getFloat(ctx, "bpm(multiplier)");
                        int delayLevel = getInteger(ctx, "delayLevel(Optional)");
                        float prevBPM = BPMManager.multiplyBPM(bpm, delayLevel);
                        ctx.getSource().sendMessage(Text.literal("Modified from " + prevBPM + " bpm to " + BPMManager.getBPM() + " bpm with repeater delay level " + BPMManager.getDelayLevel()));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
        );

        builder.then(literal("reset")
            .executes(ctx -> {
                BPMManager.setBPM(150.0f, 4);
                ctx.getSource().sendMessage(Text.literal("Reset to " + BPMManager.getBPM() + " bpm with repeater delay level " + BPMManager.getDelayLevel() + ". (vanilla)"));
                return Command.SINGLE_SUCCESS;
            })
        );

        builder.then(literal("status")
            .executes(ctx -> {
                ctx.getSource().sendMessage(Text.literal("Base BPM: " + BPMManager.getBaseBPM() + "\nCurrent BPM: " + BPMManager.getBPM() + "\nRepeater delay level: " + BPMManager.getDelayLevel()));
                return Command.SINGLE_SUCCESS;
            })
        );

        dispatcher.register(builder);
    }
}
