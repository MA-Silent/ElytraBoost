package dev.ruitenbeek.elytraboost.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraboostClient implements ClientModInitializer {

    public static final String MOD_ID = "elytraboost";
    public static final ModMetadata MOD_META;
    public static final Logger LOG;
    public static MinecraftClient mc;

    static {
        MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
        LOG = LoggerFactory.getLogger(MOD_META.getName());
    }

    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();
        LoadModules.loadAll();
    }

}
