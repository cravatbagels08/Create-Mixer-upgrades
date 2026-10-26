package dev.mixerupgrades;

import java.util.List;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MixerUpgradeItem extends Item {

	private final MixerUpgradeTier tier;

	public MixerUpgradeItem(MixerUpgradeTier tier, Properties properties) {
		super(properties);
		this.tier = tier;
	}

	public MixerUpgradeTier getTier() {
		return tier;
	}

	// Runs before the block's own interaction, so the mixer never sees the click
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!(level.getBlockEntity(pos) instanceof MechanicalMixerBlockEntity mixer))
			return InteractionResult.PASS;

		MixerUpgradeHolder holder = (MixerUpgradeHolder) mixer;
		Player player = context.getPlayer();
		int previousLevel = holder.mixerupgrades$getUpgradeLevel();

		if (previousLevel == tier.level()) {
			if (player != null && !level.isClientSide)
				player.displayClientMessage(Component.translatable("message." + MixerUpgrades.MOD_ID + ".already_installed")
					.withStyle(ChatFormatting.RED), true);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (level.isClientSide)
			return InteractionResult.SUCCESS;

		holder.mixerupgrades$setUpgradeLevel(tier.level());
		mixer.notifyUpdate();

		boolean creative = player != null && player.getAbilities().instabuild;
		if (!creative) {
			stack.shrink(1);
			// Swapping kits hands the old one back
			MixerUpgradeTier previous = MixerUpgradeTier.byLevel(previousLevel);
			if (previous != null && player != null)
				player.getInventory().placeItemBackInInventory(previous.toStack());
		}

		level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.8f, 1.2f);
		if (player != null)
			player.displayClientMessage(Component.translatable("message." + MixerUpgrades.MOD_ID + ".installed",
				tier.displayTier(), tier.batchSize()), true);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		String key = "tooltip." + MixerUpgrades.MOD_ID;
		tooltip.add(tier.displayTier());
		tooltip.add(Component.translatable(key + ".batch", Component.literal(String.valueOf(tier.batchSize()))
			.withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(key + ".install").withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(Component.translatable(key + ".remove").withStyle(ChatFormatting.DARK_GRAY));
	}
}
