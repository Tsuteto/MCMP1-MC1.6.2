package tsuteto.mcmp.mcmp1;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.songselector.SongSelector;
import tsuteto.mcmp.core.songselector.SongSelectorRandom;
import tsuteto.mcmp.core.util.ModLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMCMP1 extends ItemMcmpPlayer
{
    private Icon iconStopped;
    private Icon iconPlaying;

    private SongSelector playAction;

    public ItemMCMP1(int itemId)
    {
        super(itemId);

        playAction = new SongSelectorRandom(this);
    }

    /**
     * Action when right-clicking
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            isPlaying ^= true;
        }
        ModLog.debug("playing: %s", isPlaying);

        if (isPlaying)
        {
            player.worldObj.playSoundAtEntity(player, getSoundOnPlay(), 1.0F, 1.0F);
        }
        else
        {
            player.worldObj.playSoundAtEntity(player, getSoundOnStop(), 1.0F, 1.0F);
            stop(itemstack, player);
            setNoInterval();
        }
        return itemstack;
    }

    /**
     * Event on item update
     */
    @Override
    public void onPlayerUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }

        boolean sndPlaying = sndMgr.playing();
        EntityPlayer player = (EntityPlayer) entity;

        if (isPlaying && !sndPlaying)
        {
            if (timeInterval > 0)
            {
                timeInterval--;
            }
            else
            {
                ItemStack nextSong = playAction.selectSongToPlay(itemstack, player.inventory);
                if (nextSong != null)
                {
                    play(itemstack, player, nextSong);
                }
                else
                {
                    stop(itemstack, player);
                    playPos.slotPlaying = 0;
                }
                timeInterval = 20;
            }
        }
    }

	@Override
    protected void onPlay(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
        this.itemIcon = this.iconPlaying;
    }

    @Override
    protected void onStop(ItemStack mcmp, EntityPlayer player)
    {
        this.itemIcon = this.iconStopped;
    }

    @Override
    public String getSoundOnPlay()
    {
        return "mcmp1:play";
    }

    @Override
    public String getSoundOnStop()
    {
        return "mcmp1:stop";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconPlaying = par1IconRegister.registerIcon("mcmp1:playerPlaying");
        this.iconStopped = par1IconRegister.registerIcon("mcmp1:playerStopped");
        this.itemIcon = this.iconStopped;
    }
}
