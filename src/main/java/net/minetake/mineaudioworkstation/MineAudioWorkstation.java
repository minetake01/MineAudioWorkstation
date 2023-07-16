package net.minetake.mineaudioworkstation;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minetake.mineaudioworkstation.commands.PlaySpeedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MineAudioWorkstation implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("mineaudioworkstation");

	public static final Block EXPANDED_NOTE_BLOCK = new ExpandedNoteBlock(FabricBlockSettings.create().strength(4.0f));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			BPMManager.server = server;
			BPMManager.load();
		});

		Registry.register(Registries.BLOCK, new Identifier("mineaudioworkstation", "expanded_note_block"), EXPANDED_NOTE_BLOCK);
		Registry.register(Registries.ITEM, new Identifier("mineaudioworkstation", "expanded_note_block"), new BlockItem(EXPANDED_NOTE_BLOCK, new Item.Settings()));

		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
			PlaySpeedCommand.register(dispatcher);
		}));

		LOGGER.info("Hello Fabric world!");
	}
}
