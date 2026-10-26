package dev.mixerupgrades;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class MixerUpgradeInteractions {

	/** Sneak + right-click a mixer with an empty hand takes the upgrade kit out. */
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();
		if (event.getHand() != InteractionHand.MAIN_HAND || !player.isShiftKeyDown() || !event.getItemStack().isEmpty())
			return;

		Level level = event.getLevel();
		if (!(level.getBlockEntity(event.getPos()) instanceof MechanicalMixerBlockEntity mixer))
			return;
		MixerUpgradeHolder holder = (MixerUpgradeHolder) mixer;
		MixerUpgradeTier tier = MixerUpgradeTier.byLevel(holder.mixerupgrades$getUpgradeLevel());
		if (tier == null)
			return;

		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
		if (level.isClientSide)
			return;

		holder.mixerupgrades$setUpgradeLevel(0);
		mixer.notifyUpdate();
		player.getInventory().placeItemBackInInventory(tier.toStack());
		level.playSound(null, event.getPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8f, 0.9f);
		player.displayClientMessage(Component.translatable("message." + MixerUpgrades.MOD_ID + ".removed", tier.displayTier()), true);
	}
}
