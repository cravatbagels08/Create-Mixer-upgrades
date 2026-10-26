package dev.mixerupgrades.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;

import dev.mixerupgrades.MixerUpgradeHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

/** Stores the installed upgrade level. Saved with the block and synced to clients for the goggle tooltip. */
@Mixin(MechanicalMixerBlockEntity.class)
public abstract class MechanicalMixerBlockEntityMixin implements MixerUpgradeHolder {

	@Unique
	private static final String mixerupgrades$TAG = "MixerUpgradeLevel";

	@Unique
	private int mixerupgrades$upgradeLevel;

	@Override
	public int mixerupgrades$getUpgradeLevel() {
		return mixerupgrades$upgradeLevel;
	}

	@Override
	public void mixerupgrades$setUpgradeLevel(int level) {
		mixerupgrades$upgradeLevel = level;
	}

	@Inject(method = "write", at = @At("TAIL"))
	private void mixerupgrades$write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
		if (mixerupgrades$upgradeLevel > 0)
			tag.putInt(mixerupgrades$TAG, mixerupgrades$upgradeLevel);
	}

	@Inject(method = "read", at = @At("TAIL"))
	private void mixerupgrades$read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
		mixerupgrades$upgradeLevel = tag.getInt(mixerupgrades$TAG);
	}
}
