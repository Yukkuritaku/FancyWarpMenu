package com.github.yukkuritaku.modernwarpmenu.data.layout;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LayoutProvider implements DataProvider {

    private final List<LayoutFile> layouts = Collections.synchronizedList(new LinkedList<>());
    private final PackOutput.PathProvider pathProvider;
    private final String modid;

    public LayoutProvider(FabricDataOutput output, String modid) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "layouts");
        this.modid = modid;
    }

    public record LayoutFile(Layout layout, String fileName) {
    }

    protected Path getPath(ResourceLocation id) {
        return this.pathProvider.json(id);
    }

    private CompletableFuture<?> generateFeatures(CachedOutput cache) {
        CompletableFuture<?>[] completableFutures = new CompletableFuture<?>[this.layouts.size()];
        int size = 0;
        for (var layout : this.layouts) {
            var target = getPath(ResourceLocation.fromNamespaceAndPath(this.modid, layout.fileName));
            completableFutures[size++] = DataProvider.saveStable(cache, Layout.CODEC.codec().encodeStart(JsonOps.INSTANCE, layout.layout).getOrThrow(), target);
        }

        return CompletableFuture.allOf(completableFutures);
    }

    private void addLayouts() {
        this.layouts.add(new LayoutFile(new Layout(Layout.LayoutType.OVERWORLD, Layout.EMPTY,
                List.of(
                        new Island("Hub",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/hub.png"),
                                        883, 674),
                                21, 17, 0, 0.35f,
                                List.of(
                                        new Warp(19, 11, "Spawn", "hub"),
                                        new Warp(17, 25, "Museum", "museum"),
                                        new Warp(3, 15, "Crypts", "crypts"),
                                        new Warp(26, 18, "Wizard", "wizard"),
                                        new Warp(37, 28, "Sirius (DA)", "da"),
                                        new Warp(1, 22, "Ruins", "castle")
                                )
                        ),
                        new Island("Crimson Isle",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/crimson_isle.png"), 926, 656),
                                22, 1, 2, 0.25f,
                                List.of(
                                        new Warp(4, 30, "Spawn", "isle"),
                                        new Warp(22, 8, "Skull", "skull"),
                                        new Warp(27, 14, "Tomb", "smold"),
                                        new Warp(12, 12, "Wasteland", "wasteland", List.of("bingo")),
                                        new Warp(11, 2, "Dragontail", "dragontail", List.of("bingo")),
                                        new Warp(21, 24, "Scarleton", "scarleton", List.of("bingo"))
                                )),
                        new Island("Spider's Den",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/spiders_den.png"), 790, 522),
                                12, 12, 1, 0.175f,
                                List.of(
                                        new Warp(23, 23, "Spawn", "spider"),
                                        new Warp(31, 1, "Top", "top"),
                                        new Warp(6, 16, "Arachne", "arachne")
                                )),
                        new Island("The End",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/end.png"), 707, 667),
                                1, 1, 2, 0.2f,
                                List.of(
                                        new Warp(26, 20, "Spawn", "end"),
                                        new Warp(11, 32, "Nest", "drag"),
                                        new Warp(24, 30, "Void", "void")
                                )
                        ),
                        new Island("Gold Mine",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/gold_mine.png"), 464, 493),
                                36, 9, 1, 0.10f,
                                List.of(new Warp(8, 20, "Spawn", "gold"))
                        ),
                        new Island("Deep Caverns",
                                new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                        "textures/gui/islands/deep_caverns.png"), 397, 509),
                                41, 2, 2, 0.12f,
                                List.of(
                                        new Warp(5, 21, "Spawn", "deep"),
                                        new Warp(32, 12, "Dwarven\nMines", "dwarves"),
                                        new Warp(17, 5, "Forge", "forge"),
                                        new Warp(31, 26, "Hollows", "ch"),
                                        new Warp(18, 30, "Nucleus", "nucleus")
                                )),
                        new Island("Home", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/private_island.png"), 414, 488),
                                45, 27, 1, 0.05f,
                                List.of(new Warp(3, 3, "Spawn", "home"))
                        ),
                        new Island("Garden", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/garden.png"), 941, 569),
                                50, 27, 2, 0.10f,
                                List.of(new Warp(10, 8, "Spawn", "garden"))),
                        new Island("The Barn", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/barn.png"), 1080, 1080),
                                43, 15, 1, 0.11f,
                                List.of(new Warp(1, 19, "Spawn", "barn"))
                        ),
                        new Island("Mushroom Desert", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/mushroom_desert.png"), 827, 604),
                                50, 7, 2, 0.2f,
                                List.of(new Warp(14, 29, "Spawn", "desert"),
                                        new Warp(12, 8, "Trapper", "trapper"))
                                ),
                        new Island("The Park", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/park.png"), 537, 560),
                                10, 22, 0, 0.15f,
                                List.of(new Warp(25, 30, "Spawn", "park"),
                                        new Warp(30, 16, "Cave", "howl"),
                                        new Warp(19, 4, "Jungle", "jungle"))),
                        new Island("Jerry's Workshop", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/jerrys_workshop.png"), 890, 602),
                                2, 16, 2, 0.15f,
                                List.of(new Warp(22, 16, "Jerry", "jerry",
                                        List.of("jerry")))),
                        new Island("Dungeon Hub", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                                "textures/gui/islands/dungeon_hub.png"), 256, 512),
                                4, 26, 2, 0.03f,
                                List.of(new Warp(6, 15, "Spawn", "dungeons"))
                                )

                ),
                new WarpIcon(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(
                        ModernWarpMenu.MOD_ID, "textures/gui/portal.png"), 207, 256),
                        0.02f),
                new Button(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                        "icon.png"), 512, 512), 60, 31, 0.05f
                ),
                new Button(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                        "textures/gui/regular_warp_menu.png"), 128, 64), 60, 29, 0.05f
                )
        ),
                "layout"));
        this.layouts.add(new LayoutFile(new Layout(
                Layout.LayoutType.RIFT,
                Layout.EMPTY,
                List.of(
                        new Island("Rift", new LayoutTexture(ResourceLocation.fromNamespaceAndPath(
                                ModernWarpMenu.MOD_ID, "textures/gui/islands/rift.png"), 974, 1051),
                                21, 6, 0, 0.35f,
                                List.of(
                                        new Warp(18, 25, "Wizard Tower", 10),
                                        new Warp(8, 23, "Lagoon Hut", 11),
                                        new Warp(18, 11, "Dreadfarm", 12),
                                        new Warp(22, 19, "Plaza", 13),
                                        new Warp(11, 18, "Colosseum", 14),
                                        new Warp(31, 28, "Stillgore Château", 15)
                                ))
                ),
                new WarpIcon(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(
                        ModernWarpMenu.MOD_ID, "textures/gui/portal.png"), 207, 256),
                        0.02f),
                new Button(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                        "icon.png"), 512, 512), 60, 31, 0.05f
                ),
                new Button(new LayoutTexture(ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID,
                        "textures/gui/regular_warp_menu.png"), 128, 64), 60, 29, 0.05f
                )), "rift_layout"));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        this.addLayouts();
        return CompletableFuture.allOf(generateFeatures(output));
    }

    @Override
    public String getName() {
        return "layouts";
    }
}
