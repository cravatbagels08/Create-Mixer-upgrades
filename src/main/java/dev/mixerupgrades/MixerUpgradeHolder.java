package dev.mixerupgrades;

/**
 * Added to Create's MechanicalMixerBlockEntity by mixin. The level is 0 for a plain mixer
 * and 1-4 for the installed {@link MixerUpgradeTier}.
 */
public interface MixerUpgradeHolder {

	int mixerupgrades$getUpgradeLevel();

	void mixerupgrades$setUpgradeLevel(int level);
}
