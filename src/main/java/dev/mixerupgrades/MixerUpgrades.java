package dev.mixerupgrades;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;

@Mod(MixerUpgrades.MOD_ID)
public class MixerUpgrades {

	public static final String MOD_ID = "create_mixer_upgrades";

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

	public static final Map<MixerUpgradeTier, DeferredItem<MixerUpgradeItem>> UPGRADES = new EnumMap<>(MixerUpgradeTier.class);

	static {
		for (MixerUpgradeTier tier : MixerUpgradeTier.values())
			UPGRADES.put(tier, ITEMS.register(tier.itemName(), () -> new MixerUpgradeItem(tier, upgradeProperties(tier))));
	}

	// Transitional items for the sequenced assembly recipes
	public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_BRASS_UPGRADE =
		ITEMS.register("incomplete_brass_mixer_upgrade", () -> new SequencedAssemblyItem(new Item.Properties()));
	public static final DeferredItem<SequencedAssemblyItem> INCOMPLETE_STURDY_UPGRADE =
		ITEMS.register("incomplete_sturdy_mixer_upgrade", () -> new SequencedAssemblyItem(new Item.Properties()));

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
		.title(Component.translatable("itemGroup." + MOD_ID))
		.icon(() -> UPGRADES.get(MixerUpgradeTier.BLAZING).toStack())
		.displayItems((params, output) -> UPGRADES.values().forEach(output::accept))
		.build());

	public MixerUpgrades(IEventBus modBus, ModContainer container) {
		ITEMS.register(modBus);
		TABS.register(modBus);
		container.registerConfig(ModConfig.Type.COMMON, MixerUpgradeConfig.SPEC);
		NeoForge.EVENT_BUS.register(MixerUpgradeInteractions.class);
	}

	private static Item.Properties upgradeProperties(MixerUpgradeTier tier) {
		Item.Properties properties = new Item.Properties().stacksTo(16);
		switch (tier) {
			case BRASS -> properties.rarity(Rarity.COMMON);
			case PRECISION -> properties.rarity(Rarity.UNCOMMON);
			case STURDY -> properties.rarity(Rarity.RARE);
			case BLAZING -> properties.rarity(Rarity.EPIC).fireResistant();
		}
		return properties;
	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
