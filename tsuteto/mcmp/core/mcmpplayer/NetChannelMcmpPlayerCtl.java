package tsuteto.mcmp.core.mcmpplayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import tsuteto.mcmp.core.INetChannelHandler;
import tsuteto.mcmp.core.McmpMain;
import tsuteto.mcmp.core.util.ModLog;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;

/**
 * A packet handler for the channel MCMP player control
 *
 * @author Tsuteto
 *
 */
public class NetChannelMcmpPlayerCtl implements INetChannelHandler
{
    @Override
    public void onChannelData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        ItemStack itemstack = McmpPlayerManager.getActivePlayer();

        if (itemstack == null || !(itemstack.getItem() instanceof ItemMcmpPlayer))
            return;

        ItemMcmpPlayer mcmpPlayer = (ItemMcmpPlayer) itemstack.getItem();

        ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);

        try
        {
            int type = (int) data.readByte();
            switch (type)
            {
            case ItemMcmpPlayer.PKT_TYPE_STATE:
                mcmpPlayer.playPos.slotPlaying = data.readInt();
                mcmpPlayer.playPos.playingInStack = data.readInt();
                ModLog.debug(mcmpPlayer.playPos);
                break;
            case ItemMcmpPlayer.PKT_TYPE_PLAY:
                mcmpPlayer.play(itemstack, (EntityPlayer) player, null);
                break;
            case ItemMcmpPlayer.PKT_TYPE_STOP:
                mcmpPlayer.stop(itemstack, (EntityPlayer) player);
                break;
            }
            mcmpPlayer.receiveAdditionalCtlPacketData(type, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
