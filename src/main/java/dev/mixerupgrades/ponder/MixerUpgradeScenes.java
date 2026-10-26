package dev.mixerupgrades.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import dev.mixerupgrades.MixerUpgradeHolder;
import dev.mixerupgrades.MixerUpgradeTier;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class MixerUpgradeScenes {

	/**
	 * Every text() call here needs a matching create_mixer_upgrades.ponder.mixer_upgrade.text_N entry in
	 * en_us.json, numbered in call order.
	 */
	public static void upgradeKits(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("mixer_upgrade", "Upgrading the Mechanical Mixer");
		scene.configureBasePlate(0, 0, 5);

		BlockPos basin = util.grid().at(2, 1, 2);
		BlockPos mixer = util.grid().at(2, 3, 2);
		Vec3 mixerTop = util.vector().topOf(mixer);
		Vec3 mixerSide = util.vector().blockSurface(mixer, Direction.WEST);
		Vec3 basinSide = util.vector().blockSurface(basin, Direction.WEST);

		scene.world().showSection(util.select().layer(0), Direction.UP);
		scene.idle(5);
		scene.world().showSection(util.select().position(basin), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().fromTo(3, 1, 2, 3, 3, 2), Direction.DOWN);
		scene.world().showSection(util.select().position(mixer), Direction.DOWN);
		scene.world().setKineticSpeed(util.select().fromTo(3, 1, 2, 3, 3, 2), 64);
		scene.world().setKineticSpeed(util.select().position(mixer), -64);
		scene.idle(20);

		scene.overlay().showText(70)
			.pointAt(mixerSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Upgrade Kits let a Mechanical Mixer run its recipe many times in a single cycle");
		scene.idle(80);

		// Installing
		scene.overlay().showControls(mixerTop, Pointing.DOWN, 40)
			.rightClick()
			.withItem(MixerUpgradeTier.BRASS.toStack());
		scene.idle(10);
		setLevel(scene, mixer, MixerUpgradeTier.BRASS);
		scene.overlay().showOutline(PonderPalette.GREEN, mixer, util.select().position(mixer), 40);
		scene.overlay().showText(60)
			.pointAt(mixerSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Right-click the Mixer with a Kit to install it");
		scene.idle(70);

		// Tiers and swapping
		scene.overlay().showText(90)
			.pointAt(mixerSide)
			.placeNearTarget()
			.text("Each tier runs up to 16, 32, 48 or 64 recipes per cycle. Installing a new Kit hands the old one back");
		for (MixerUpgradeTier tier : new MixerUpgradeTier[] { MixerUpgradeTier.PRECISION, MixerUpgradeTier.STURDY, MixerUpgradeTier.BLAZING }) {
			scene.overlay().showControls(mixerTop, Pointing.DOWN, 20)
				.rightClick()
				.withItem(tier.toStack());
			scene.idle(10);
			setLevel(scene, mixer, tier);
			scene.idle(20);
		}
		scene.idle(20);

		// A full batch in one cycle
		ItemStack andesite = new ItemStack(Items.ANDESITE, 64);
		ItemStack nuggets = new ItemStack(Items.IRON_NUGGET, 64);
		ItemStack alloy = new ItemStack(AllItems.ANDESITE_ALLOY.get(), 64);
		scene.overlay().showControls(util.vector().topOf(basin), Pointing.LEFT, 40).withItem(andesite);
		scene.overlay().showControls(util.vector().topOf(basin), Pointing.RIGHT, 40).withItem(nuggets);
		scene.idle(10);
		scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
			SmartInventory input = be.getInputInventory();
			input.setStackInSlot(0, andesite.copy());
			input.setStackInSlot(1, nuggets.copy());
		});
		scene.idle(20);

		scene.overlay().showText(80)
			.pointAt(basinSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("With the Blazing Kit, one cycle turns 64 sets of ingredients into 64 results");
		scene.world().modifyBlockEntity(mixer, MechanicalMixerBlockEntity.class, MechanicalMixerBlockEntity::startProcessingBasin);
		scene.idle(50);
		scene.world().modifyBlockEntity(basin, BasinBlockEntity.class, be -> {
			SmartInventory input = be.getInputInventory();
			for (int slot = 0; slot < input.getSlots(); slot++)
				input.setStackInSlot(slot, ItemStack.EMPTY);
			be.getOutputInventory().setStackInSlot(0, alloy.copy());
		});
		scene.idle(40);
		scene.overlay().showControls(util.vector().topOf(basin), Pointing.UP, 40).withItem(alloy);
		scene.idle(50);

		scene.overlay().showText(70)
			.pointAt(basinSide)
			.placeNearTarget()
			.text("The batch stops early if the Basin runs out of ingredients or output space");
		scene.idle(80);

		// Removing
		scene.overlay().showControls(mixerTop, Pointing.DOWN, 40)
			.rightClick()
			.whileSneaking();
		scene.idle(10);
		setLevel(scene, mixer, null);
		scene.world().createItemEntity(util.vector().centerOf(mixer).add(-0.6, 0, 0), new Vec3(-0.1, 0.25, 0),
			MixerUpgradeTier.BLAZING.toStack());
		scene.overlay().showText(70)
			.pointAt(mixerSide)
			.placeNearTarget()
			.attachKeyFrame()
			.text("Sneak and right-click with an empty hand to take the Kit back out. Breaking the Mixer drops it too");
		scene.idle(80);
	}

	private static void setLevel(CreateSceneBuilder scene, BlockPos mixer, MixerUpgradeTier tier) {
		int level = tier == null ? 0 : tier.level();
		scene.world().modifyBlockEntity(mixer, MechanicalMixerBlockEntity.class,
			be -> ((MixerUpgradeHolder) be).mixerupgrades$setUpgradeLevel(level));
	}
}
