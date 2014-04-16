package tsuteto.mcmp.core.eventhandler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import tsuteto.mcmp.core.McmpMain;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CommonTickHandler implements ITickHandler
{

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc.thePlayer != null)
        {
            for (ItemMcmpPlayer mcmpPlayer : McmpPlayerManager.getPlayerList())
            {
                if (!mcmpPlayer.inInventory && mcmpPlayer.isPlaying
                        && !mc.thePlayer.inventory.hasItem(mcmpPlayer.itemID))
                {
                    mcmpPlayer.stop(null, mc.thePlayer);
                }
                else
                {
                    mcmpPlayer.inInventory = false;
                }
            }
        }
     }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return null;
    }

}
