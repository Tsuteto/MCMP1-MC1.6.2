package tsuteto.mcmp.changer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import tsuteto.mcmp.core.INetChannelHandler;
import cpw.mods.fml.common.network.Player;

public class NetChannelChangerRename implements INetChannelHandler
{
    @Override
    public void onChannelData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        EntityPlayer entityplayer = (EntityPlayer)player;

        String var3 = (new String(packet.data)).trim();

        if (entityplayer.getCurrentEquippedItem() != null)
        {
            ItemStack var4 = entityplayer.getCurrentEquippedItem();
            InventoryChanger var5 = new InventoryChanger(entityplayer, var4, true);
            var5.setChangerName(var3);
            var5.saveInventory();
        }
    }
}
