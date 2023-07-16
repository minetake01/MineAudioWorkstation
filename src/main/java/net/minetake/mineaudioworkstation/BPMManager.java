package net.minetake.mineaudioworkstation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class BPMManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("mineaudioworkstation");
    public static MinecraftServer server = null;
    private static float BASE_BPM = 150.0f;
    private static float BPM = 150.0f;
    private static int DELAY_LEVEL = 4;
    public static float MSPT = 50.0f;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void setBPM(float newBPM, int newDelayLevel) {
        BASE_BPM = newBPM;
        BPM = newBPM;
        DELAY_LEVEL = newDelayLevel;
        updateMSPT();
    }

    public static void setBPM(float newBPM) {
        BASE_BPM = newBPM;
        BPM = newBPM;
        updateMSPT();
    }

    public static void setDelayLevel(int newDelayLevel) {
        DELAY_LEVEL = newDelayLevel;
        updateMSPT();
    }

    public static float multiplyBPM(float multiplier, int newDelayLevel) {
        float prevBPM = BPM;
        DELAY_LEVEL = newDelayLevel;
        BPM = BASE_BPM * multiplier;
        updateMSPT();
        return prevBPM;
    }

    public static float multiplyBPM(float multiplier) {
        float prevBPM = BPM;
        BPM = BASE_BPM * multiplier;
        updateMSPT();
        return prevBPM;
    }

    private static void updateMSPT() {
        MSPT = 30000f / (DELAY_LEVEL * BPM);
    }

    public static float getBaseBPM() {
        return BASE_BPM;
    }

    public static float getBPM() {
        return BPM;
    }

    public static int getDelayLevel() {
        return DELAY_LEVEL;
    }

    public static float getMSPT() {
        return MSPT;
    }

    public static void save() {
        Path worldDir = server.getSavePath(WorldSavePath.ROOT);
        File file = new File(String.valueOf(worldDir), "mineaudioworkstation.json");
        try {
            if (file.createNewFile()) {
                LOGGER.info("Created BPM file.");
            }
            FileWriter writer = new FileWriter(file);
            BPMData data = new BPMData(BASE_BPM, BPM, DELAY_LEVEL);
            GSON.toJson(data, writer);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to save BPM. ", e);
        }
    }

    public static void load() {
        Path worldDir = server.getSavePath(WorldSavePath.ROOT);
        File file = new File(String.valueOf(worldDir), "mineaudioworkstation.json");
        try {
            FileReader reader = new FileReader(file);
            BPMData data = GSON.fromJson(reader, BPMData.class);
            BASE_BPM = data.baseBPM;
            BPM = data.bpm;
            DELAY_LEVEL = data.delayLevel;
        } catch (IOException e) {
            LOGGER.error("Failed to load BPM. ", e);
            BASE_BPM = 150.0f;
            BPM = 150.0f;
            DELAY_LEVEL = 4;
        }
        updateMSPT();
    }

    private static class BPMData {
        float baseBPM;
        float bpm;
        int delayLevel;

        BPMData(float baseBPM, float bpm, int delayLevel) {
            this.baseBPM = baseBPM;
            this.bpm = bpm;
            this.delayLevel = delayLevel;
        }
    }
}
