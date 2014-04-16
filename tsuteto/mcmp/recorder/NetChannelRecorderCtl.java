package tsuteto.mcmp.recorder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tsuteto.mcmp.cassettetape.ItemCassetteTape.Source;
import tsuteto.mcmp.core.INetChannelHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.Player;

public class NetChannelRecorderCtl implements INetChannelHandler
{
    @Override
    public void onChannelData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
        int x, y, z;
        byte inputSourceId;
        int selectedRow;
        try
        {
            x = data.readInt();
            y = data.readInt();
            z = data.readInt();
            inputSourceId = data.readByte();
            selectedRow = data.readInt();

            World world = ((EntityPlayer)player).worldObj;
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

            if (tileEntity instanceof TileEntityRecorder)
            {
                TileEntityRecorder tileEntityRecorder = (TileEntityRecorder)tileEntity;
                tileEntityRecorder.setInputSource(Source.values()[inputSourceId]);
                tileEntityRecorder.setSonglistRowSelected(selectedRow);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
