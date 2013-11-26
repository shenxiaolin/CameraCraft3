package de.take_weiland.mods.cameracraft.tileentity;

import de.take_weiland.mods.cameracraft.api.cable.NetworkNode;
import de.take_weiland.mods.cameracraft.api.cable.NetworkTile;
import de.take_weiland.mods.cameracraft.networking.NetworkNodeImpl;
import de.take_weiland.mods.cameracraft.networking.NetworkUtil;
import de.take_weiland.mods.commons.templates.AbstractTileEntity;

public class TileEntityDataCable extends AbstractTileEntity implements NetworkTile {

	private NetworkNode node = new NetworkNodeImpl(this);
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!node.hasNetwork()) {
			NetworkUtil.initializeNetworking(this);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		NetworkUtil.shutdownNetworking(this);
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public NetworkNode getNode() {
		return node;
	}

}
