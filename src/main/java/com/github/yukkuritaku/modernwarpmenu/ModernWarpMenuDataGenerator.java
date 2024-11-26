package com.github.yukkuritaku.modernwarpmenu;

import com.github.yukkuritaku.modernwarpmenu.data.layout.LayoutProvider;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.SkyBlockConstantsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModernWarpMenuDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider((packOutput, future) ->
				new LayoutProvider(packOutput, ModernWarpMenu.MOD_ID));
		pack.addProvider((packOutput, future) ->
				new SkyBlockConstantsProvider(packOutput, ModernWarpMenu.MOD_ID));
	}
}
