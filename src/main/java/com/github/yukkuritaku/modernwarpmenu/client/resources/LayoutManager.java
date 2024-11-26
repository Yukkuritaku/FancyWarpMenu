package com.github.yukkuritaku.modernwarpmenu.client.resources;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Warp;
import com.github.yukkuritaku.modernwarpmenu.data.layout.WarpIcon;
import com.github.yukkuritaku.modernwarpmenu.state.ModernWarpMenuState;
import com.github.yukkuritaku.modernwarpmenu.utils.ChatUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.Map;

public class LayoutManager extends SimplePreparableReloadListener<LayoutManager.LayoutList> implements IdentifiableResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    private static void loadLayout(Resource resource, ResourceLocation layoutId, ImmutableMap.Builder<ResourceLocation, Layout> builder) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
            Layout layout = Layout.CODEC.codec().parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
            builder.put(layoutId, layout);
        } catch (Exception e) {
            handleLoadException(resource, layoutId, e);
        }
    }

    private static void handleLoadException(Resource resource, ResourceLocation location, Exception e) {
        CrashReport crashReport = new CrashReport("Your Modern Warp Menu resource pack may be outdated", e);
        CrashReportCategory resourceCategory = crashReport.addCategory("Resource");
        CrashReportCategory resourcePackCategory = crashReport.addCategory("Resource Pack");
        resourceCategory.setDetail("Path", location.toString());
        resourcePackCategory.setDetail("Name", resource.source().location().title().getString());
        throw new ReportedException(crashReport);
    }

    @Override
    protected LayoutList prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, Layout> layoutBuilder = ImmutableMap.builder();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("layouts",
                resourceLocation ->
                        resourceLocation.getNamespace().equalsIgnoreCase(ModernWarpMenu.MOD_ID) &&
                                resourceLocation.getPath().endsWith(".json"));

        for (var entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            loadLayout(entry.getValue(), location, layoutBuilder);
        }
        return new LayoutList(layoutBuilder.build());
    }

    @Override
    protected void apply(LayoutList object, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (var layoutEntry : object.layouts.entrySet()) {
            WarpIcon icon = layoutEntry.getValue().warpIcon();
            Warp.setWarpIcon(icon);
            Layout layout = layoutEntry.getValue();
            switch (layout.layoutType()) {
                case OVERWORLD -> ModernWarpMenuState.setOverworldLayout(layout);
                case RIFT -> ModernWarpMenuState.setRiftLayout(layout);
            }
        }
        object.layouts.forEach((key, value) -> LOGGER.info("Layout loaded {}", key));
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "constants");
    }

    public record LayoutList(Map<ResourceLocation, Layout> layouts) {
    }
}
