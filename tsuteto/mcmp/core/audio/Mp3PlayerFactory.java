package tsuteto.mcmp.core.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Mp3PlayerFactory
{
    public static Mp3Player playMp3(File mp3, float volume) throws IOException, JavaLayerException
    {
        return playMp3(mp3, 0, Integer.MAX_VALUE, volume);
    }

    public static Mp3Player playMp3(File mp3, int start, int end, float volume) throws IOException, JavaLayerException
    {
        return playMp3(new BufferedInputStream(new FileInputStream(mp3)), start, end, volume);
    }

    public static Mp3Player playMp3(final InputStream stream, final int start, final int end, float volume)
            throws JavaLayerException
    {
        final Mp3Player player = new Mp3Player(stream, volume);

        Thread playingThread = new Thread() {
            public void run()
            {
                try
                {
                    player.play(start, end);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e.getMessage());
                }
            }
        };
        playingThread.setName("MCMP-1 MP3 Player");
        playingThread.start();
        return player;
    }
}
