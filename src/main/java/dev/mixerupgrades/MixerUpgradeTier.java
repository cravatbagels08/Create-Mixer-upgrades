package dev.mixerupgrades;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public enum MixerUpgradeTier {
	BRASS("brass", "I", ChatFormatting.GOLD),
	PRECISION("precision", "II", ChatFormatting.YELLOW),
	STURDY("sturdy", "III", ChatFormatting.LIGHT_PURPLE),
	BLAZING("blazing", "IV", ChatFormatting.RED);

	private final String name;
	private final String numeral;
	private final ChatFormatting color;

	MixerUpgradeTier(String name, String numeral, ChatFormatting color) {
		this.name = name;
		this.numeral = numeral;
		this.color = color;
	}

	/** 1-based level stored on the mixer; 0 means no upgrade installed. */
	public int level() {
		return ordinal() + 1;
	}

	public String itemName() {
		return name + "_mixer_upgrade";
	}

	public int batchSize() {
		return MixerUpgradeConfig.batchSize(this);
	}

	public ItemStack toStack() {
		return MixerUpgrades.UPGRADES.get(this).toStack();
	}

	/** "Tier II", colored to match the kit. */
	public Component displayTier() {
		return Component.translatable("tooltip." + MixerUpgrades.MOD_ID + ".tier", numeral).withStyle(color);
	}

	@Nullable
	public static MixerUpgradeTier byLevel(int level) {
		MixerUpgradeTier[] values = values();
		if (level < 1 || level > values.length)
			return null;
		return values[level - 1];
	}

	/** How many times a mixer at this level runs its recipe per cycle (1 when not upgraded). */
	public static int batchSizeFor(int level) {
		MixerUpgradeTier tier = byLevel(level);
		return tier == null ? 1 : tier.batchSize();
	}
}
