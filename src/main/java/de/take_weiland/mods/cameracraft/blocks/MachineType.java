package de.take_weiland.mods.cameracraft.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import de.take_weiland.mods.cameracraft.gui.CCGuis;
import de.take_weiland.mods.cameracraft.tileentity.*;
import de.take_weiland.mods.commons.meta.Subtype;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public enum MachineType implements Subtype {
	
	PHOTO_PROCESSOR("processor", TilePhotoProcessor.class, CCGuis.PHOTO_PROCESSOR),
	ITEM_MUTATOR("oreDictionary", TileItemMutator.class, CCGuis.ORE_DICTIONARY),
	PRINTER("printer", TilePrinter.class, CCGuis.PRINTER),
	PRINTER_ADVANCED("printerAdvanced", TilePrinter.class, CCGuis.PRINTER_ADVANCED),
	SCANNER("scanner", TileScanner.class, CCGuis.SCANNER),
	CAMERA("camera", TileCamera.class, CCGuis.CAMERA_PLACED);

	private final String name;
	private final Class<? extends TileEntity> teClass;
	private final CCGuis gui;
	
	private MachineType(String name, CCGuis gui) {
		this(name, null, gui);
	}
	
	private MachineType(String name, Class<? extends TileEntity> teClass, CCGuis gui) {
		this.name = name;
		this.teClass = teClass;
		this.gui = gui;
	}
	
	@Override
	public String subtypeName() {
		return name;
	}

	public void openGui(EntityPlayer player, int x, int y, int z) {
		if (gui != null) {
			gui.open(player, x, y, z);
		}
	}

	public boolean hasTileEntity() {
		return teClass != null;
	}
	
	public TileEntity createTileEntity() {
		try {
			return teClass.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to create TileEntity instance for " + this + "!", e);
		}
	}
	
	public static void registerTileEntities() {
		for (MachineType type : values()) {
			if (type.hasTileEntity()) {
				GameRegistry.registerTileEntity(type.teClass, "cameracraft." + type.name);
			}
		}
	}
}
