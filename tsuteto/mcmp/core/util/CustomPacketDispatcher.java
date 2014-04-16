package tsuteto.mcmp.core.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.PacketDispatcher;

public class CustomPacketDispatcher
{
    private String channelName;
    private Packet250CustomPayload packet = new Packet250CustomPayload();
    private boolean packed = false;

    private ByteArrayOutputStream bytesStream;
    private DataOutputStream dataStream;

    public static CustomPacketDispatcher create(String channelName)
    {
        return new CustomPacketDispatcher(channelName);
    }

    private CustomPacketDispatcher(String channelName)
    {
        this.channelName = channelName;

        this.bytesStream = new ByteArrayOutputStream();
        this.dataStream = new DataOutputStream(bytesStream);
    }

    public CustomPacketDispatcher addInt(int val)
    {
        try
        {
            dataStream.writeInt(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addBoolean(boolean val)
    {
        try
        {
            dataStream.writeBoolean(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addByte(byte val)
    {
        try
        {
            dataStream.writeByte(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addShort(short val)
    {
        try
        {
            dataStream.writeShort(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addDouble(double val)
    {
        try
        {
            dataStream.writeDouble(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addFloat(float val)
    {
        try
        {
            dataStream.writeFloat(val);
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher setString(String str)
    {
        try
        {
            dataStream.write(str.getBytes());
        }
        catch (IOException e)
        {
            ModLog.log(Level.WARNING, e, "Failed to set value to a packet.");
        }
        return this;
    }

    public CustomPacketDispatcher addData(DataHandler handler)
    {
        if (handler == null)
        {
            throw new RuntimeException("No data handler given!");
        }

        try
        {
            handler.addData(dataStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this;
    }

    public CustomPacketDispatcher setChunkDataPacket()
    {
        packet.isChunkDataPacket = true;
        return this;
    }

    public void dispatch()
    {
        if (!this.packed)
            this.pack();
        PacketDispatcher.sendPacketToServer(packet);
    }

    public Packet250CustomPayload getPacket()
    {
        if (!this.packed)
            this.pack();
        return this.packet;
    }

    public void pack()
    {
        this.packet.channel = this.channelName;
        this.packet.data = bytesStream.toByteArray();
        this.packet.length = bytesStream.size();
        this.packed = true;
    }

    public static interface DataHandler
    {
        void addData(DataOutputStream dos) throws Exception;
    }
}
