package dev.mixerupgrades;

import java.util.EnumMap;
import java.util.Map;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MixerUpgradeConfig {

	private static final int[] DEFAULT_BATCH_SIZES = { 16, 32, 48, 64 };
	private static final Map<MixerUpgradeTier, ModConfigSpec.IntValue> BATCH_SIZES = new EnumMap<>(MixerUpgradeTier.class);

	public static final ModConfigSpec SPEC;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder.comment("How many times an upgraded Mechanical Mixer runs its recipe in one mixing cycle.",
				"The mixer still stops early when the basin runs out of ingredients or output space.")
			.push("batch_sizes");
		for (MixerUpgradeTier tier : MixerUpgradeTier.values())
			BATCH_SIZES.put(tier, builder.defineInRange(tier.name().toLowerCase() + "_tier",
				DEFAULT_BATCH_SIZES[tier.ordinal()], 1, 1024));
		builder.pop();
		SPEC = builder.build();
	}

	public static int batchSize(MixerUpgradeTier tier) {
		if (!SPEC.isLoaded())
			return DEFAULT_BATCH_SIZES[tier.ordinal()];
		return BATCH_SIZES.get(tier).get();
	}
}
