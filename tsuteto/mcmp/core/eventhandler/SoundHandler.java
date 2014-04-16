package tsuteto.mcmp.core.eventhandler;

import java.util.List;
import java.util.logging.Level;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import tsuteto.tofu.util.ModLog;

import com.google.common.collect.Lists;

public class SoundHandler
{
    @ForgeSubscribe
    public void onSoundLoad(SoundLoadEvent event)
    {
        List<String> files = Lists.newArrayList();

        files.add("play.ogg");
        files.add("stop.ogg");

        for (String file : files)
        {
            try
            {
                event.manager.addSound("mcmp1:" + file);
            }
            catch (Exception e)
            {
                ModLog.log(Level.SEVERE, e, "Failed to load sound file: %s", files);
            }
        }
    }
}