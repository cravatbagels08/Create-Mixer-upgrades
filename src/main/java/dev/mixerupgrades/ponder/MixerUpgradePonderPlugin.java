package dev.mixerupgrades.ponder;

import java.util.ArrayList;
import java.util.List;

import dev.mixerupgrades.MixerUpgrades;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;

public class MixerUpgradePonderPlugin implements PonderPlugin {

	@Override
	public String getModId() {
		return MixerUpgrades.MOD_ID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		// Hold W over any kit, or over Create's own mixer, to see the scene
		List<ResourceLocation> components = new ArrayList<>();
		for (DeferredItem<?> kit : MixerUpgrades.UPGRADES.values())
			components.add(kit.getId());
		components.add(ResourceLocation.fromNamespaceAndPath("create", "mechanical_mixer"));

		helper.forComponents(components)
			.addStoryBoard("mixer_upgrade", MixerUpgradeScenes::upgradeKits);
	}
}
