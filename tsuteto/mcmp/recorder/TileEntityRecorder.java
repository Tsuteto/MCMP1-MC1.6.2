package tsuteto.mcmp.recorder;

import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.cassettetape.ItemCassetteTape.Source;
import tsuteto.mcmp.core.McmpMain;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.song.MediaSongEntry;
import tsuteto.mcmp.core.song.SongManager;
import tsuteto.mcmp.core.util.CustomPacketDispatcher;

public class TileEntityRecorder extends TileEntity implements IInventory
{
    /**
     * 0=Dubbing, 1=To be dubbed, 2=Done dubbing
     */
    private ItemStack machineContents[];

    /** The number of ticks that the current item has been dubbing for */
    public int dubbingTime;
    private Source inputSource;
    public int posSonglist = 0;
    private int songlistRowSelected = -1;
    private String hddFileNameSelected;
    public static final int timeRequiredForDubbing = 160;

    private String customName;

    public TileEntityRecorder()
    {
        machineContents = new ItemStack[3];
        dubbingTime = 0;
        inputSource = Source.HDD;
        hddFileNameSelected = null;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return this.dispatchControlPacket();
    }

    public void setInputSource(Source source)
    {
        this.inputSource = source;
    }

    public Source getInputSource()
    {
        return this.inputSource;
    }

    public void setSonglistRowSelected(int row)
    {
        this.songlistRowSelected = row;

        if (row != -1)
        {
            SongManager songManager = McmpSoundManager.getInstance().getSongManager();
            this.hddFileNameSelected = songManager.getSongList().get(this.songlistRowSelected).file.getName();
        }
        else
        {
            this.hddFileNameSelected = null;
        }
    }

    public int getSonglistRowSelected()
    {
        return this.songlistRowSelected;
    }

    public String getHddFileNameSelected()
    {
        return this.hddFileNameSelected;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return machineContents.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return machineContents[par1];
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of
     * the second int arg. Returns the new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.machineContents[par1] != null)
        {
            ItemStack var3;

            if (this.machineContents[par1].stackSize <= par2)
            {
                var3 = this.machineContents[par1];
                this.machineContents[par1] = null;
                this.onInventoryChanged();
                return var3;
            }
            else
            {
                var3 = this.machineContents[par1].splitStack(par2);

                if (this.machineContents[par1].stackSize == 0)
                {
                    this.machineContents[par1] = null;
                }

                this.onInventoryChanged();
                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (machineContents[par1] != null)
        {
            ItemStack itemstack = machineContents[par1];
            machineContents[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        machineContents[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > getInventoryStackLimit())
        {
            par2ItemStack.stackSize = getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInvName()
    {
        return this.isInvNameLocalized() ? this.customName : "container.mcmp1.Recorder";
    }

    @Override
    public boolean isInvNameLocalized() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String par1Str)
    {
        this.customName = par1Str;
    }

    public Packet dispatchControlPacket()
    {
        return CustomPacketDispatcher.create(McmpMain.netChannelRecorderCtl)
                .addData(new CustomPacketDispatcher.DataHandler() {

                    @Override
                    public void addData(DataOutputStream dos) throws Exception
                    {
                        dos.writeInt(xCoord);
                        dos.writeInt(yCoord);
                        dos.writeInt(zCoord);
                        dos.writeByte((byte)getInputSource().ordinal());
                        dos.writeInt(songlistRowSelected);
                    }

                })
                .setChunkDataPacket()
                .getPacket();
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        machineContents = new ItemStack[getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);
            int slotNo = nbttagcompound.getByte("Slot") & 255;

            if (slotNo >= 0 && slotNo < machineContents.length)
            {
                machineContents[slotNo] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }

        dubbingTime = par1NBTTagCompound.getShort("DubbingTime");
        inputSource = Source.values()[par1NBTTagCompound.getByte("InputSource")];
        hddFileNameSelected = par1NBTTagCompound.getString("SongSelected");
        if (hddFileNameSelected.length() == 0)
        {
            hddFileNameSelected = null;
        }

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.customName = par1NBTTagCompound.getString("CustomName");
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("DubbingTime", (short) dubbingTime);
        par1NBTTagCompound.setByte("InputSource", (byte) inputSource.ordinal());
        if (hddFileNameSelected != null)
        {
            par1NBTTagCompound.setString("SongSelected", hddFileNameSelected);
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < machineContents.length; i++)
        {
            if (machineContents[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                machineContents[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);

        if (this.isInvNameLocalized())
        {
            par1NBTTagCompound.setString("CustomName", this.customName);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Returns an integer between 0 and the passed value representing how close
     * the current item is to being completely cooked
     */
    public int getDubbingProgressScaled(int par1)
    {
        return (dubbingTime * par1) / getSongLength();
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses,
     * e.g. the mob spawner uses this to count ticks and creates a new spawn
     * inside its implementation.
     */
    @Override
    public void updateEntity()
    {
        boolean flag = isDubbing();
        boolean flag1 = false;

        if (!worldObj.isRemote)
        {
            if (canDub())
            {
                dubbingTime++;

                if (dubbingTime >= getSongLength())
                {
                    dubbingTime = 0;
                    dubItem();
                    hddFileNameSelected = null;
                    songlistRowSelected = -1;
                    flag1 = true;
                }
            }
            else
            {
                dubbingTime = 0;
                int m = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 3;
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 2);
            }

            if (flag != isDubbing())
            {
                flag1 = true;
                int m = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

                if (isDubbing())
                {
                    m = (m & 3) + 4;
                }
                else
                {
                    m = m & 3;
                }

                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 2);
            }

            if (flag != isDubbing())
            {
                flag1 = true;
                BlockRecorder.updateFurnaceBlockState(isDubbing(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }

        if (flag1)
        {
            onInventoryChanged();
        }
    }

    public boolean isDubbing()
    {
        return dubbingTime > 0;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item,
     * destination stack isn't full, etc.
     */
    private boolean canDub()
    {
        if (machineContents[0] == null || !(machineContents[0].getItem() instanceof ItemCassetteTape))
        {
            return false;
        }

        if (inputSource == Source.RECORDS
                && (machineContents[1] == null || !(machineContents[1].getItem() instanceof ItemRecord)))
        {
            return false;
        }

        if (inputSource == Source.HDD && hddFileNameSelected == null)
        {
            return false;
        }
        if (machineContents[2] != null)
        {
            return false;
        }
        return true;
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted
     * item in the furnace result stack
     */
    public void dubItem()
    {
        if (!canDub())
        {
            return;
        }

        MediaSongEntry newEntry = null;
        if (inputSource == Source.HDD)
        {
            newEntry = new MediaSongEntry(inputSource, hddFileNameSelected);
        }
        else if (inputSource == Source.RECORDS)
        {
            String songId = ((ItemRecord) machineContents[1].getItem()).recordName;
            newEntry = new MediaSongEntry(inputSource, songId);
        }
        ItemStack newItemstack = machineContents[0].copy();
        newItemstack.stackSize = 1;
        ItemCassetteTape.setSong(newItemstack, newEntry);

        if (machineContents[2] == null)
        {
            machineContents[2] = newItemstack.copy();

        }
        else if (machineContents[2].itemID == newItemstack.itemID)
        {
            machineContents[2].stackSize += newItemstack.stackSize;
        }

        machineContents[0].stackSize--;

        if (machineContents[0].stackSize <= 0)
        {
            machineContents[0] = null;
        }
    }

    /**
     * Returns the length of the selected song
     */
    public static int getSongLength()
    {
        return timeRequiredForDubbing;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
        {
            return false;
        }

        return par1EntityPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}
}
