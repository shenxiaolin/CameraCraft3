package de.take_weiland.mods.cameracraft.client.gui.printer;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import de.take_weiland.mods.cameracraft.api.printer.PrintJob;
import de.take_weiland.mods.cameracraft.gui.ContainerPrinter;
import de.take_weiland.mods.cameracraft.gui.ContainerPrinter.ClientNodeInfo;
import de.take_weiland.mods.cameracraft.network.PacketPrintJob;
import de.take_weiland.mods.cameracraft.tileentity.TilePrinter;
import de.take_weiland.mods.commons.client.AbstractGuiContainer;
import de.take_weiland.mods.commons.client.Guis;
import de.take_weiland.mods.commons.client.ScrollPane;

public class GuiPrinter extends AbstractGuiContainer<TilePrinter, ContainerPrinter> {

	private static final int BUTTON_OPEN_SCROLLER = 0;
	public static final int BUTTON_PRINT = 1;
	
	private static boolean isScrollerOpen = false;
	
	int sliderToggleDelay = -1;
	int scrollerOffsetX = isScrollerOpen ? 0 : getScrollerHiddenOffset();
	private int scrollerMotion = 0;
	
	private ScrollPane networkScroller;
	private ScrollPane photoIdScroller;
	
	
	
	public GuiPrinter(ContainerPrinter container) {
		super(container);
	}

	int getScrollerHiddenOffset() {
		return - (xSize - 16);
	}
	
	@Override
	protected ResourceLocation provideTexture() {
		return new ResourceLocation("cameracraft:textures/gui/printer.png");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		ySize = 200;
		super.initGui();
		networkScroller = new NetworkListScroller(this, guiLeft + 8, guiTop + 15, xSize - 16, 70, 0);
		photoIdScroller = new PhotoSelectionScroller(this, guiLeft + 8, guiTop + 15, xSize - 16, 70, 0);
		photoIdScroller.setClip(true);
		
		buttonList.add(new ButtonOpenScroller(BUTTON_OPEN_SCROLLER, guiLeft + 4, guiTop + 15 + 30));
		buttonList.add(new GuiButton(GuiPrinter.BUTTON_PRINT, 0, 40, 50, 20, "Print!"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		glDisable(GL_LIGHTING);
		
		List<ClientNodeInfo> nodes = container.getNodes();
		networkScroller.setContentHeight(nodes == null ? 0 : nodes.size() * 10);
		
		if (scrollerOffsetX != 0) {
			if (scrollerOffsetX != getScrollerHiddenOffset()) {
				networkScroller.setClip(false);
				int scale = Guis.computeGuiScale();
				glPushMatrix();
				glEnable(GL_SCISSOR_TEST);
				glScissor((guiLeft + 8) * scale, mc.displayHeight - (guiTop + 15 + 70) * scale, (xSize - 16 + guiLeft + 8) * scale, 70 * scale);
				glTranslatef(scrollerOffsetX + (partialTicks * 40 * scrollerMotion), 0, 0);
				
				networkScroller.draw(mouseX, mouseY);
				glDisable(GL_SCISSOR_TEST);
				glPopMatrix();
			}
		} else {
			networkScroller.setClip(true);
			networkScroller.draw(mouseX, mouseY);
		}
		
		if (shouldDrawIds()) {
			photoIdScroller.setContentHeight(container.getSelectedNode().photoIds.length * 10);
			photoIdScroller.draw(mouseX, mouseY);
		}
		
		glEnable(GL_LIGHTING);
	}
	
	private boolean shouldDrawIds() {
		return scrollerOffsetX == getScrollerHiddenOffset() && container.getSelectedNode() != null;
	}
	

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		switch (button.id) {
		case BUTTON_OPEN_SCROLLER:
			if (scrollerMotion == 0) {
				toggleSlider();
			}
			break;
		case BUTTON_PRINT:
			String selectedId = container.getSelectedPhotoId();
			if (selectedId != null) {
				new PacketPrintJob(container, new PrintJob(selectedId)).sendToServer();
			}
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int dWheel = Mouse.getEventDWheel();
		networkScroller.onMouseWheel(dWheel);
		photoIdScroller.onMouseWheel(dWheel);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int btn) {
		super.mouseClicked(mouseX, mouseY, btn);
		if (isScrollerOpen) {
			networkScroller.onMouseClick(mouseX, mouseY, btn);
		} else if (shouldDrawIds()) {
			photoIdScroller.onMouseClick(mouseX, mouseY, btn);
		}
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int btn) {
		super.mouseMovedOrUp(mouseX, mouseY, btn);
		if (isScrollerOpen) {
			networkScroller.onMouseBtnReleased(btn);
		} else if (shouldDrawIds()) {
			photoIdScroller.onMouseBtnReleased(btn);
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int btn, long time) {
		super.mouseClickMove(mouseX, mouseY, btn, time);
		if (isScrollerOpen) {
			networkScroller.onMouseMoved(mouseX, mouseY);
		} else if (shouldDrawIds()) {
			photoIdScroller.onMouseMoved(mouseX, mouseY);
		}
	}

	private void toggleSlider() {
		scrollerMotion = -Integer.signum(scrollerOffsetX + 1);
		isScrollerOpen = !isScrollerOpen;
	}

	@Override
	public void updateScreen() {
		if (sliderToggleDelay >= 0) {
			if (--sliderToggleDelay == -1) {
				toggleSlider();
			}
		}
		
		scrollerOffsetX += scrollerMotion * 40;
		if (scrollerOffsetX <= getScrollerHiddenOffset() || scrollerOffsetX >= 0) {
			scrollerOffsetX = MathHelper.clamp_int(scrollerOffsetX, getScrollerHiddenOffset(), 0);
			scrollerMotion = 0;
		}
		super.updateScreen();
	}
	
	private class ButtonOpenScroller extends GuiButton {
		
		ButtonOpenScroller(int id, int x, int y) {
			super(id, x, y, 4, 7, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			GuiPrinter.this.bindTexture();
			drawTexturedModalRect(xPosition, yPosition, 176 + (scrollerOffsetX == getScrollerHiddenOffset() ? 0 : 4), 0, 4, 7);
		}
		
	}

}
