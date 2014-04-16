package tsuteto.mcmp.core.eventhandler;

import java.util.EnumSet;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.src.ModLoader;
import tsuteto.mcmp.core.McmpMain;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerTickHandler implements ITickHandler
{
    private McmpMain mod;

    public PlayerTickHandler(McmpMain mod)
    {
        this.mod = mod;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (tickData[1] != null)
        {
            mod.onTickInGUI((Float) tickData[0], ModLoader.getMinecraftInstance(), (GuiScreen) tickData[1]);
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
