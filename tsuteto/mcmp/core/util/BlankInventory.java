package tsuteto.mcmp.core.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BlankInventory implements IInventory
{
    private ItemStack itemstack;

    public BlankInventory(ItemStack itemstack)
    {
        this.itemstack = itemstack;
    }

    @Override
    public int getSizeInventory()
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int var1)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
    }

    @Override
    public String getInvName()
    {
        return null;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    @Override
    public void onInventoryChanged()
    {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return true;
    }

    @Override
    public void openChest()
    {

    }

    @Override
    public void closeChest()
    {
    }

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

}
