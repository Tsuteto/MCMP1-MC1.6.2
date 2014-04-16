package tsuteto.mcmp.core;

import java.util.Map;
import java.util.TreeMap;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class McmpPacketHandler implements IPacketHandler
{
    public static Map<String, INetChannelHandler> handlerRegistry = new TreeMap<String, INetChannelHandler>();

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        INetChannelHandler handler = handlerRegistry.get(packet.channel);
        if (handler != null)
        {
            handler.onChannelData(manager, packet, player);
        }
    }

}
