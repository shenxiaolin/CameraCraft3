package de.take_weiland.mods.cameracraft.item;

import static de.take_weiland.mods.cameracraft.item.PhotoStorageType.FILM_B_W;
import static de.take_weiland.mods.cameracraft.item.PhotoStorageType.FILM_COLOR;
import static de.take_weiland.mods.cameracraft.item.PhotoStorageType.MEMORY_CARD;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import de.take_weiland.mods.cameracraft.api.photo.PhotoStorageItem;
import de.take_weiland.mods.cameracraft.api.photo.PhotoStorage;
import de.take_weiland.mods.cameracraft.photo.PhotoStorages;
import de.take_weiland.mods.commons.util.ItemStacks;
import de.take_weiland.mods.commons.util.Multitypes;

public class ItemPhotoStorages extends CCItemMultitype<PhotoStorageType> implements PhotoStorageItem {

	public ItemPhotoStorages(int defaultId) {
		super("photoStorage", defaultId);
		setMaxStackSize(1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "boxing" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean verbose) {
		PhotoStorageType type = Multitypes.getType(this, stack);
		String key = type.isSealed() ? "item.CameraCraft.photoStorage.subtext" : "item.CameraCraft.photoStorage.subtext.sealed";
		int size = PhotoStorages.fastSize(stack);
		if (size == 1) {
			key += ".single";
		}
		text.add(I18n.getStringParams(key, size, type.getCapacity()));
	}

	@Override
	public PhotoStorageType[] getTypes() {
		return PhotoStorageType.VALUES;
	}

	@Override
	protected List<ItemStack> provideSubtypes() {
		return ItemStacks.of(FILM_B_W, FILM_COLOR, MEMORY_CARD);
	}

	@Override
	public PhotoStorage getPhotoStorage(ItemStack stack) {
		return Multitypes.getType(this, stack).getStorage(stack);
	}

	@Override
	public boolean isSealed(ItemStack stack) {
		return Multitypes.getType(this, stack).isSealed();
	}

	@Override
	public ItemStack unseal(ItemStack sealed) {
		return applyMeta(sealed, Multitypes.getType(this, sealed).getUnsealed());
	}
	
	@Override
	public boolean canRewind(ItemStack stack) {
		return Multitypes.getType(this, stack).canRewind();
	}

	@Override
	public ItemStack rewind(ItemStack stack) {
		return applyMeta(stack, Multitypes.getType(this, stack).rewind());
	}
	
	@Override
	public boolean canBeProcessed(ItemStack stack) {
		return Multitypes.getType(this, stack).canProcess();
	}

	@Override
	public ItemStack process(ItemStack stack) {
		return applyMeta(stack, Multitypes.getType(this, stack).getProcessed());
	}
	
	@Override
	public boolean isScannable(ItemStack stack) {
		return Multitypes.getType(this, stack).isScannable();
	}
	
	private static ItemStack applyMeta(ItemStack stack, PhotoStorageType meta) {
		ItemStack n = ItemStacks.of(meta);
		if (stack.hasTagCompound()) {
			n.stackTagCompound = (NBTTagCompound) stack.stackTagCompound.copy();
		}
		return n;
	}

}
