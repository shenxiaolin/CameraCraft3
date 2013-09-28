package de.take_weiland.mods.cameracraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Slot;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import de.take_weiland.mods.cameracraft.gui.ContainerCamera;
import de.take_weiland.mods.cameracraft.inv.InventoryCamera;
import de.take_weiland.mods.commons.client.AbstractGuiContainer;
import de.take_weiland.mods.commons.client.Rendering;

public class GuiCamera extends AbstractGuiContainer<InventoryCamera, ContainerCamera> {

	private int lidXSize = 0;
	private int lidMovement = 0;
	private boolean wasClosed;
	
	public GuiCamera(ContainerCamera container) {
		super(container);
		wasClosed = container.inventory().isLidClosed();
		lidXSize = wasClosed ? 16 : 0;
		lidMovement = wasClosed ? 1 : -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(ContainerCamera.BUTTON_TOGGLE_LID, 10, 10, 80, 20, "ToggleLid"));
	}

	@Override
	protected ResourceLocation provideTexture() {
		return container.inventory().getType().getGuiTexture();
	}

	@Override
	protected void drawSlotInventory(Slot slot) {
		if (shouldDrawLid(slot)) {
			if (lidXSize != 16) {
				super.drawSlotInventory(slot);
			}
			int x = slot.xDisplayPosition;
			int y = slot.yDisplayPosition;
			Rendering.drawRectWithZlevel(x, y, x + lidXSize, y + 16, 101, 0xff000000); // zlevel 101 to go above the itemstacks
		} else {
			super.drawSlotInventory(slot);
		}
	}

	private boolean shouldDrawLid(Slot slot) {
		InventoryCamera inv = container.inventory();
		return slot.inventory == inv && slot.getHasStack() && inv.storageSlot() == slot.getSlotIndex() && (inv.isLidClosed() || lidXSize != 0);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		boolean isClosed = container.inventory().isLidClosed();
		if (isClosed != wasClosed) {
			wasClosed = isClosed;
			lidMovement = isClosed ? 1 : -1;
		}
		lidXSize = MathHelper.clamp_int(lidXSize + lidMovement, 0, 16);
	}

}