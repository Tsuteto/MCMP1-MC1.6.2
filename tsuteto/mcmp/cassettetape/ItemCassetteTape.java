package tsuteto.mcmp.cassettetape;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.song.MediaSongEntry;
import tsuteto.mcmp.core.song.SongInfo;

public class ItemCassetteTape extends Item
{
    public static final int[] colors = new int[] {
        0x1e1b1b, // black
        0xe83929, // red
        0x3cb371, // green
        0x965042, // brown
        0x8080ff, // blue
        0xb872db, // purple
        0x40e0d0, // cyan
        0xc0c0c8, // silver
        0x808080, // gray
        0xffc0cb, // pink
        0x60ff60, // lime
        0xdecf2a, // yellow
        0xa0d8ef, // lightBlue
        0xe4007f, // magenta
        0xeb8844, // orange
        0xffffff // white
    };

    private McmpSoundManager sndMgr;

    public ItemCassetteTape(int par1)
    {
        super(par1);
        sndMgr = McmpSoundManager.getInstance();
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[getDyeTypeFromDamage(par1ItemStack.getItemDamage())];
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return colors[getDyeTypeFromDamage(par1ItemStack.getItemDamage())];
    }

    public static int getDyeTypeFromDamage(int par0)
    {
        return ~par0 & 15;
    }

    public static int getDamageFromDyeType(int par0)
    {
        return ~par0 & 15;
    }

    // @Override
    // public void addCreativeItems(ArrayList itemList) {
    // for (int i = 0; i < 16; i++) {
    // itemList.add(new ItemStack(this, 1, i));
    // }
    // }

    public static void setSong(ItemStack itemstack, MediaSongEntry entry)
    {
        if (itemstack.stackTagCompound == null)
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.stackTagCompound.setTag("mcmp", new NBTTagList("mcmp"));

        NBTTagList nbttaglist = (NBTTagList) itemstack.stackTagCompound.getTag("mcmp");
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setByte("s", (byte) entry.source.ordinal());
        nbttagcompound.setString("t", entry.id);
        nbttaglist.appendTag(nbttagcompound);
    }

    public static MediaSongEntry getSong(ItemStack itemstack)
    {
        NBTTagCompound stackTagCompound = itemstack.getTagCompound();

        if (stackTagCompound != null && stackTagCompound.hasKey("mcmp"))
        {
            NBTTagList nbttaglist = (NBTTagList) stackTagCompound.getTag("mcmp");
            if (nbttaglist != null)
            {
                return new MediaSongEntry(
                        ((NBTTagCompound) nbttaglist.tagAt(0)).getByte("s"),
                        ((NBTTagCompound) nbttaglist.tagAt(0)).getString("t")
                        );
            }
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
    {
        MediaSongEntry song = getSong(itemstack);
        if (song == null)
        {
            return;
        }
        if (song.source == Source.HDD)
        {
            SongInfo info = sndMgr.getSongManager().getSongInfo(song);
            if (info != null)
            {
                list.add(info.songName);
            }
            else
            {
                list.add(StatCollector.translateToLocalFormatted("mcmp1.fileNotFound", song.id));
            }
        }
        else if (song.source == Source.RECORDS)
        {
            ItemRecord itemrecord = ItemRecord.getRecord(song.id);
            if (itemrecord != null)
            {
                list.add(itemrecord.getRecordTitle());
            }
            else
            {
                list.add(StatCollector.translateToLocalFormatted("mcmp1.recordNotFound", song.id));
            }
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(int var1, CreativeTabs var2, List var3)
    {
        for (int var4 = 0; var4 < 16; ++var4)
        {
            var3.add(new ItemStack(var1, 1, var4));
        }
    }

    public enum Source
    {
        RECORDS, HDD;
    }
}
