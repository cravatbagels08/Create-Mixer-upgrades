package dev.mixerupgrades.test;

import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;

import dev.mixerupgrades.MixerUpgradeHolder;
import dev.mixerupgrades.MixerUpgradeTier;
import dev.mixerupgrades.MixerUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@GameTestHolder(MixerUpgrades.MOD_ID)
@PrefixGameTestTemplate(false)
public class MixerUpgradeGameTests {

	private static final BlockPos BASIN = new BlockPos(2, 1, 2);
	private static final BlockPos MIXER = BASIN.above(2);
	private static final BlockPos COG = MIXER.east();
	private static final BlockPos MOTOR = COG.above();

	@GameTest(template = "empty", timeoutTicks = 600)
	public static void plainMixerRunsOncePerCycle(GameTestHelper helper) {
		mixAndExpectFirstCycle(helper, 0, 1);
	}

	@GameTest(template = "empty", timeoutTicks = 600)
	public static void brassUpgradeRunsSixteenPerCycle(GameTestHelper helper) {
		mixAndExpectFirstCycle(helper, MixerUpgradeTier.BRASS.level(), 16);
	}

	@GameTest(template = "empty", timeoutTicks = 600)
	public static void sturdyUpgradeRunsFortyEightPerCycle(GameTestHelper helper) {
		mixAndExpectFirstCycle(helper, MixerUpgradeTier.STURDY.level(), 48);
	}

	@GameTest(template = "empty", timeoutTicks = 600)
	public static void blazingUpgradeRunsSixtyFourPerCycle(GameTestHelper helper) {
		mixAndExpectFirstCycle(helper, MixerUpgradeTier.BLAZING.level(), 64);
	}

	/** Only 10 sets of ingredients: the batch stops when the basin runs dry instead of failing. */
	@GameTest(template = "empty", timeoutTicks = 600)
	public static void batchStopsWhenIngredientsRunOut(GameTestHelper helper) {
		buildRig(helper, MixerUpgradeTier.BLAZING.level(), 10);
		helper.startSequence()
			.thenWaitUntil(() -> helper.assertTrue(countOutput(helper) > 0, "Mixer has not finished a cycle yet"))
			.thenExecute(() -> helper.assertTrue(countOutput(helper) == 10, "Expected 10 alloy, got " + countOutput(helper)))
			.thenExecute(() -> helper.assertTrue(countInput(helper) == 0, "Basin should be empty"))
			.thenSucceed();
	}

	@GameTest(template = "empty")
	public static void kitInstallsSwapsAndDropsOnBreak(GameTestHelper helper) {
		helper.setBlock(MIXER, AllBlocks.MECHANICAL_MIXER.getDefaultState());
		MixerUpgradeHolder holder = (MixerUpgradeHolder) helper.getBlockEntity(MIXER);
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);

		ItemStack sturdy = MixerUpgradeTier.STURDY.toStack();
		useOnMixer(helper, player, sturdy);
		helper.assertTrue(holder.mixerupgrades$getUpgradeLevel() == 3, "Sturdy kit should set level 3");
		helper.assertTrue(sturdy.isEmpty(), "Kit should be consumed");

		ItemStack blazing = MixerUpgradeTier.BLAZING.toStack();
		useOnMixer(helper, player, blazing);
		helper.assertTrue(holder.mixerupgrades$getUpgradeLevel() == 4, "Blazing kit should replace the sturdy kit");
		helper.assertTrue(player.getInventory().countItem(item(MixerUpgradeTier.STURDY)) == 1,
			"Old kit should be handed back");

		helper.destroyBlock(MIXER);
		helper.assertItemEntityPresent(item(MixerUpgradeTier.BLAZING), MIXER, 2);
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void upgradeRecipesLoad(GameTestHelper helper) {
		for (String id : List.of("sequenced_assembly/brass_mixer_upgrade", "mechanical_crafting/precision_mixer_upgrade",
			"sequenced_assembly/sturdy_mixer_upgrade", "mechanical_crafting/blazing_mixer_upgrade"))
			helper.assertTrue(helper.getLevel().getRecipeManager().byKey(MixerUpgrades.asResource(id)).isPresent(),
				"Recipe did not load: " + id);
		helper.succeed();
	}

	private static void mixAndExpectFirstCycle(GameTestHelper helper, int level, int expected) {
		buildRig(helper, level, 64);
		helper.startSequence()
			.thenWaitUntil(() -> helper.assertTrue(countOutput(helper) > 0, "Mixer has not finished a cycle yet"))
			.thenExecute(() -> helper.assertTrue(countOutput(helper) == expected,
				"First cycle should make " + expected + " alloy, made " + countOutput(helper)))
			.thenSucceed();
	}

	/** Creative motor -> cogwheel -> mixer over a basin holding andesite and iron nuggets. */
	private static void buildRig(GameTestHelper helper, int level, int ingredientSets) {
		helper.setBlock(BASIN, AllBlocks.BASIN.getDefaultState());
		helper.setBlock(MIXER, AllBlocks.MECHANICAL_MIXER.getDefaultState());
		helper.setBlock(COG, AllBlocks.COGWHEEL.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y));
		helper.setBlock(MOTOR, AllBlocks.CREATIVE_MOTOR.getDefaultState().setValue(CreativeMotorBlock.FACING, Direction.DOWN));
		((CreativeMotorBlockEntity) helper.getBlockEntity(MOTOR)).generatedSpeed.setValue(128);

		((MixerUpgradeHolder) helper.getBlockEntity(MIXER)).mixerupgrades$setUpgradeLevel(level);

		IItemHandler basin = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(BASIN), null);
		ItemHandlerHelper.insertItem(basin, new ItemStack(Items.ANDESITE, ingredientSets), false);
		ItemHandlerHelper.insertItem(basin, new ItemStack(Items.IRON_NUGGET, ingredientSets), false);
	}

	private static void useOnMixer(GameTestHelper helper, Player player, ItemStack stack) {
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		BlockPos pos = helper.absolutePos(MIXER);
		stack.onItemUseFirst(new UseOnContext(player, InteractionHand.MAIN_HAND,
			new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
	}

	private static int countOutput(GameTestHelper helper) {
		return count(((BasinBlockEntity) helper.getBlockEntity(BASIN)).getOutputInventory(), AllItems.ANDESITE_ALLOY.get());
	}

	private static int countInput(GameTestHelper helper) {
		SmartInventory inv = ((BasinBlockEntity) helper.getBlockEntity(BASIN)).getInputInventory();
		int total = 0;
		for (int slot = 0; slot < inv.getSlots(); slot++)
			total += inv.getStackInSlot(slot).getCount();
		return total;
	}

	private static int count(SmartInventory inv, Item item) {
		int total = 0;
		for (int slot = 0; slot < inv.getSlots(); slot++)
			if (inv.getStackInSlot(slot).is(item))
				total += inv.getStackInSlot(slot).getCount();
		return total;
	}

	private static Item item(MixerUpgradeTier tier) {
		return MixerUpgrades.UPGRADES.get(tier).get();
	}
}
