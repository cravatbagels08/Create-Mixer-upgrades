package dev.mixerupgrades.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import dev.mixerupgrades.MixerUpgradeHolder;
import dev.mixerupgrades.MixerUpgradeTier;
import net.minecraft.world.item.crafting.Recipe;

/**
 * At the end of a mixing cycle Create runs the recipe once. For an upgraded mixer, keep running it
 * until the batch size is reached or the basin runs out of ingredients or output space.
 */
@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin {

	@WrapOperation(method = "applyBasinRecipe", at = @At(value = "INVOKE",
		target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
	private boolean mixerupgrades$applyBatch(BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
		if (!original.call(basin, recipe))
			return false;
		// The press shares this class; only mixers carry an upgrade
		if ((Object) this instanceof MixerUpgradeHolder holder) {
			int batch = MixerUpgradeTier.batchSizeFor(holder.mixerupgrades$getUpgradeLevel());
			for (int i = 1; i < batch; i++)
				if (!original.call(basin, recipe))
					break;
		}
		return true;
	}
}
