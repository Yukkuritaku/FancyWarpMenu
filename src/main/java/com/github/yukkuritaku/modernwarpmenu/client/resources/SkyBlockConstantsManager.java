package com.github.yukkuritaku.modernwarpmenu.client.resources;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.SkyBlockConstants;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.WarpMessages;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.ItemMatchCondition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkyBlockConstantsManager implements IdentifiableResourceReloadListener {

    private static final ResourceLocation SKY_BLOCK_CONSTANTS_LOCATION =
            ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                    "constants/skyblock_constants.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private SkyBlockConstants skyBlockConstants;

    public SkyBlockConstants getSkyBlockConstants() {
        return skyBlockConstants;
    }

    private static SkyBlockConstants loadSkyBlockConstants(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
            return SkyBlockConstants.CODEC.codec().parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
        } catch (Exception e) {
            LOGGER.warn("Unable to load SkyBlockConstants '{}' in {} in resourcepack: '{}'", SkyBlockConstantsManager.SKY_BLOCK_CONSTANTS_LOCATION, "skyblockconstants.json", resource.sourcePackId(), e);
        }
        return new SkyBlockConstants(
                Map.of(),
                new WarpMessages(List.of(), Map.of()),
                List.of(),
                "");
    }

    protected CompletableFuture<SkyBlockConstants> prepare(ResourceManager resourceManager, Executor io) {
        try {
            Resource resource = resourceManager.getResourceOrThrow(SKY_BLOCK_CONSTANTS_LOCATION);
            return CompletableFuture.supplyAsync(() -> loadSkyBlockConstants(resource), io);
        } catch (IOException e) {
            LOGGER.warn("Unable to load SkyBlockConstants", e);
        }
        return CompletableFuture.supplyAsync(() -> new SkyBlockConstants(
                Map.of(),
                new WarpMessages(List.of(), Map.of()),
                List.of(),
                ""));
    }

    protected void apply(SkyBlockConstants object) {
        for (var list : object.menuMatchingMap().values()) {
            //list.sort(Comparator.comparing(ItemMatchCondition::inventorySlot));
        }
        LOGGER.info("class {}", object.menuMatchingMap().values());
        this.skyBlockConstants = object;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor io, Executor game) {
        return this.prepare(resourceManager, io)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(this::apply, game);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "skyblock_constants");
    }
}
