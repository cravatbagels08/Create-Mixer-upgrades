package dev.mixerupgrades.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.mixerupgrades.MixerUpgrades;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/**
 * Dev-only helper for checking the mod visually without a human at the keyboard. Write commands, one per
 * line, to run/devcmd.txt and they run in order on the client thread. Not part of the shipped jar.
 */
@JeiPlugin
@EventBusSubscriber(modid = MixerUpgrades.MOD_ID, value = Dist.CLIENT)
public class DevClientDriver implements IModPlugin {

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Deque<String> QUEUE = new ArrayDeque<>();
	private static IJeiRuntime jei;
	private static int waitTicks;
	private static String pendingShot;

	@Override
	public ResourceLocation getPluginUid() {
		return MixerUpgrades.asResource("dev_driver");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
		jei = runtime;
	}

	@SubscribeEvent
	public static void onTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Path file = mc.gameDirectory.toPath().resolve("devcmd.txt");
		try {
			if (Files.exists(file)) {
				QUEUE.addAll(Files.readAllLines(file));
				Files.delete(file);
			}
		} catch (IOException e) {
			LOGGER.error("devcmd read failed", e);
		}

		if (pendingShot != null)
			return;
		if (waitTicks > 0) {
			waitTicks--;
			return;
		}
		while (!QUEUE.isEmpty() && waitTicks == 0 && pendingShot == null) {
			String line = QUEUE.poll().trim();
			if (line.isEmpty())
				continue;
			try {
				run(mc, line.split(" "));
			} catch (Exception e) {
				LOGGER.error("devcmd failed: " + line, e);
			}
		}
	}

	@SubscribeEvent
	public static void onFrame(RenderFrameEvent.Post event) {
		if (pendingShot == null)
			return;
		Minecraft mc = Minecraft.getInstance();
		Screenshot.grab(mc.gameDirectory, pendingShot + ".png", mc.getMainRenderTarget(), msg -> {});
		LOGGER.info("devcmd shot saved: " + pendingShot);
		pendingShot = null;
	}

	private static void run(Minecraft mc, String[] a) throws Exception {
		switch (a[0]) {
			case "wait" -> waitTicks = Integer.parseInt(a[1]);
			// Holds the queue until the client is in a world (Ponder and most commands need one)
			case "waitplayer" -> {
				if (mc.player == null || mc.level == null) {
					QUEUE.addFirst(String.join(" ", a));
					waitTicks = 20;
				}
			}
			case "shot" -> pendingShot = a[1];
			case "slot" -> mc.player.getInventory().selected = Integer.parseInt(a[1]);
			case "look" -> {
				mc.player.setYRot(Float.parseFloat(a[1]));
				mc.player.setXRot(Float.parseFloat(a[2]));
			}
			case "use" -> {
				if (mc.hitResult instanceof BlockHitResult hit)
					mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
			}
			case "sneak" -> mc.options.keyShift.setDown(Boolean.parseBoolean(a[1]));
			case "inv" -> mc.setScreen(new InventoryScreen(mc.player));
			case "close" -> mc.setScreen(null);
			case "hoverslot" -> hoverSlot(mc, Integer.parseInt(a[1]));
			case "mouse" -> setMouse(mc, Double.parseDouble(a[1]), Double.parseDouble(a[2]));
			case "jei" -> {
				ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(a[1])));
				jei.getRecipesGui().show(jei.getJeiHelpers().getFocusFactory()
					.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack));
			}
			case "ponder" -> ScreenOpener.open(PonderUI.of(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(a[1])))));
			case "log" -> LOGGER.info("devcmd: " + String.join(" ", List.of(a).subList(1, a.length)));
			default -> LOGGER.warn("devcmd unknown: " + a[0]);
		}
	}

	/** Points the mouse at a menu slot so the screen draws its tooltip. */
	private static void hoverSlot(Minecraft mc, int index) throws Exception {
		if (!(mc.screen instanceof AbstractContainerScreen<?> screen))
			return;
		Slot slot = screen.getMenu().getSlot(index);
		double guiX = screen.getGuiLeft() + slot.x + 8;
		double guiY = screen.getGuiTop() + slot.y + 8;
		double scale = (double) mc.getWindow().getScreenWidth() / mc.getWindow().getGuiScaledWidth();
		setMouse(mc, guiX * scale, guiY * scale);
	}

	private static void setMouse(Minecraft mc, double x, double y) throws Exception {
		for (String name : new String[] { "xpos", "ypos" }) {
			Field f = MouseHandler.class.getDeclaredField(name);
			f.setAccessible(true);
			f.setDouble(mc.mouseHandler, name.equals("xpos") ? x : y);
		}
	}
}
