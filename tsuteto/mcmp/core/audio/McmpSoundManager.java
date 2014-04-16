package tsuteto.mcmp.core.audio;

import java.io.File;
import java.util.Random;

import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.settings.GameSettings;
import tsuteto.mcmp.core.song.SongInfo;
import tsuteto.mcmp.core.song.SongManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class McmpSoundManager
{
    private static McmpSoundManager instance;

    private SongManager songManager;
    private Mp3Player mp3Player;
    private SoundManagerAccessor mcSndMgr = new SoundManagerAccessor();
    private static final String bgmIdentifiedName = "BgMusic";
    private Random rand = new Random();
    /** Ticks waiting for starting up internal playing */
    private int ticksIntStarting = 0;

    public static McmpSoundManager getInstance()
    {
        if (instance == null)
        {
            instance = new McmpSoundManager();
        }
        return instance;
    }

    private McmpSoundManager()
    {
        songManager = new SongManager(getAssetsDir());
    }

    public File getAssetsDir()
    {
        return mcSndMgr.getAssetsDir();
    }

    public void registerWavFiles(String modSoundDir)
    {
        mcSndMgr.registerWavFiles(modSoundDir);
    }

    public boolean playInternal(String par1Str, SoundPool soundpool, GameSettings options)
    {
        if (mcSndMgr.sndSystem() == null)
            return false;

        // Field[] fields =
        // net.minecraft.src.SoundManager.class.getDeclaredFields();
        // System.out.println(Arrays.toString(fields));

        boolean loaded = mcSndMgr.loaded();

        if (soundpool == null)
            return false;

        if (!loaded || options.musicVolume == 0.0F)
        {
            return false;
        }

        if (playing()) stop();

        if (par1Str == null)
        {
            return false;
        }

        SoundPoolEntry soundpoolentry = soundpool.getRandomSoundFromSoundPool(par1Str);

        if (soundpoolentry != null)
        {
            mcSndMgr.sndSystem().backgroundMusic(bgmIdentifiedName, soundpoolentry.func_110457_b(), soundpoolentry.func_110458_a(), false);
            mcSndMgr.sndSystem().setVolume(bgmIdentifiedName, options.musicVolume);
            mcSndMgr.sndSystem().play(bgmIdentifiedName);
            ticksIntStarting = 20;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean playMp3(SongInfo songinfo, GameSettings options)
    {
        // if (!loaded || options.musicVolume == 0.0F) {
        // return;
        // }

        if (!songinfo.file.exists())
            return false;

        if (playing())
            stop();

        try
        {
            stopMp3();
            mp3Player = Mp3PlayerFactory.playMp3(songinfo.file, options.musicVolume);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void setMp3PlayerVolume(float volume)
    {
        if (mp3Player != null && mp3Player.playing())
        {
            mp3Player.setVolume(volume);
        }
    }

    public boolean playRecord(String str, GameSettings options)
    {
        return playInternal(str, mcSndMgr.soundPoolStreaming(), options);
    }

    public boolean playHddSong(SongInfo info, GameSettings options)
    {
        if (info.playerType == EnumSoundSystemType.INTERNAL)
        {
            return playInternal("MCMP1.songs." + info.songName, mcSndMgr.soundPoolSounds(), options);
        }
        else if (info.playerType == EnumSoundSystemType.MP3)
        {
            return playMp3(info, options);
        }
        return false;
    }

    public void stop()
    {
        if (isReady()) mcSndMgr.sndSystem().stop(bgmIdentifiedName);
        stopMp3();
    }

    public void stopMp3()
    {
        if (mp3Player != null)
        {
            mp3Player.stop();
            mp3Player = null;
        }
    }

    public boolean playing()
    {
        if (isReady())
        {
            boolean isSystemPlaying = mcSndMgr.sndSystem().playing(bgmIdentifiedName);
            if (ticksIntStarting > 0 || isSystemPlaying)
            {
                if (ticksIntStarting > 0)
                    ticksIntStarting--;
                if (ticksIntStarting > 0 && isSystemPlaying)
                {
                    ticksIntStarting = 0;
                }
                return true;
            }
        }
        if (mp3Player != null && mp3Player.playing())
        {
            if (mcSndMgr.getTicksBeforeMusic() < 10)
            {
                mcSndMgr.setTicksBeforeMusic(rand.nextInt(12000) + 12000);
            }
            return true;
        }
        return false;
    }

    public boolean isReady()
    {
        return mcSndMgr.sndSystem() != null;
    }

    public SongManager getSongManager()
    {
        return songManager;
    }
}
