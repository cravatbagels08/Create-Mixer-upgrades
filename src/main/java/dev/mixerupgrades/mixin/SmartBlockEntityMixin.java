package dev.mixerupgrades.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.mixerupgrades.MixerUpgradeHolder;
import dev.mixerupgrades.MixerUpgradeTier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Drops the installed kit when the mixer is broken (by hand, wrench, explosion...). Create removes the block
 * entity before a contraption picks the mixer up, so moved mixers keep their upgrade instead.
 */
@Mixin(SmartBlockEntity.class)
public abstract class SmartBlockEntityMixin {

	@Inject(method = "destroy", at = @At("HEAD"))
	private void mixerupgrades$dropUpgrade(CallbackInfo ci) {
		if (!((Object) this instanceof MixerUpgradeHolder holder))
			return;
		BlockEntity be = (BlockEntity) (Object) this;
		if (be.getLevel() == null || be.getLevel().isClientSide)
			return;
		MixerUpgradeTier tier = MixerUpgradeTier.byLevel(holder.mixerupgrades$getUpgradeLevel());
		if (tier == null)
			return;
		holder.mixerupgrades$setUpgradeLevel(0);
		Block.popResource(be.getLevel(), be.getBlockPos(), tier.toStack());
	}
}
