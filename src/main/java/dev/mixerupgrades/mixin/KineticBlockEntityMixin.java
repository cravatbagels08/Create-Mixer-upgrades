package dev.mixerupgrades.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import dev.mixerupgrades.MixerUpgradeHolder;
import dev.mixerupgrades.MixerUpgradeTier;
import dev.mixerupgrades.MixerUpgrades;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Shows the installed upgrade on the Engineer's Goggles overlay. */
@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin {

	@Inject(method = "addToGoggleTooltip", at = @At("RETURN"), cancellable = true)
	private void mixerupgrades$addUpgradeInfo(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
		if (!((Object) this instanceof MixerUpgradeHolder holder))
			return;
		MixerUpgradeTier tier = MixerUpgradeTier.byLevel(holder.mixerupgrades$getUpgradeLevel());
		if (tier == null)
			return;

		LangBuilder lang = new LangBuilder(MixerUpgrades.MOD_ID);
		lang.translate("goggles.upgrade")
			.forGoggles(tooltip);
		new LangBuilder(MixerUpgrades.MOD_ID)
			.add(tier.displayTier())
			.text(ChatFormatting.DARK_GRAY, " | ")
			.add(new LangBuilder(MixerUpgrades.MOD_ID)
				.translate("goggles.batch", tier.batchSize())
				.style(ChatFormatting.AQUA))
			.forGoggles(tooltip, 1);
		cir.setReturnValue(true);
	}
}
