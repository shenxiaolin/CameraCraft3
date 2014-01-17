package de.take_weiland.mods.cameracraft.client.gui.printer;

import java.util.List;

import net.minecraft.util.MathHelper;
import de.take_weiland.mods.cameracraft.gui.ContainerPrinter.ClientNodeInfo;
import de.take_weiland.mods.commons.client.ScrollPane;
import de.take_weiland.mods.commons.util.JavaUtils;

public class NetworkListScroller extends ScrollPane {
	
	private final GuiPrinter gui;

	NetworkListScroller(GuiPrinter gui, int x, int y, int width, int height, int contentHeight) {
		super(gui, x, y, width, height, contentHeight);
		this.gui = gui;
	}

	@Override
	protected void drawImpl() {
		drawRect(0, 0, width, Math.max(contentHeight, height), 0x44000000);
		
		List<ClientNodeInfo> nodes = gui.getContainer().getNodes();
		if (nodes != null) {
			int size = nodes.size();
			int selectedNode = gui.getContainer().getSelectedNodeIdx();
			for (int i = 0; i < size; ++i) {
				mc.fontRenderer.drawString(nodes.get(i).displayName, 1, 1 + i * 10, i == selectedNode ? 0x7777ff : 0xffffff);
			}
		}
	}

	@Override
	protected void handleMouseClick(int relX, int relY, int btn) {
		if (relX >= 0 && relX <= width - scrollbarWidth - 2) {
			int newSelection = MathHelper.floor_float(relY / 10f);
			
			List<ClientNodeInfo> nodes = gui.getContainer().getNodes();
			
			if (JavaUtils.listIndexExists(nodes, newSelection)) {
				mc.sndManager.playSoundFX("random.click", 1, 1);
				gui.sliderToggleDelay = 10;
				gui.getContainer().selectNode(newSelection);
			}
		}
	}
}