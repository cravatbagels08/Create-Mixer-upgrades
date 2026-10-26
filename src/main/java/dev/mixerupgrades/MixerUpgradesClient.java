package dev.mixerupgrades;

import dev.mixerupgrades.ponder.MixerUpgradePonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = MixerUpgrades.MOD_ID, dist = Dist.CLIENT)
public class MixerUpgradesClient {

	public MixerUpgradesClient() {
		PonderIndex.addPlugin(new MixerUpgradePonderPlugin());
	}
}
