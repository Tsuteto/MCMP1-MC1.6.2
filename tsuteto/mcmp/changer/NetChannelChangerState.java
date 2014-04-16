package tsuteto.mcmp.changer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import tsuteto.mcmp.core.INetChannelHandler;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;

public class NetChannelChangerState implements INetChannelHandler
{
    @Override
    public void onChannelData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);

        boolean isPlayerControl = data.readBoolean();
        int locChanger = data.readInt();
        int slotPlaying = data.readInt();
        int playingInStack = data.readInt();

        EntityPlayer entityPlayer = (EntityPlayer)player;
        ItemStack changer = entityPlayer.inventory.getStackInSlot(locChanger);
        if (changer == null || !(changer.getItem() instanceof ItemChanger))
        {
            return;
        }

        try
        {
            InventoryChanger inv =  new InventoryChanger(entityPlayer, changer, isPlayerControl);
            inv.slotPlaying = slotPlaying;
            inv.playingInStack = playingInStack;
            inv.saveInventory();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
