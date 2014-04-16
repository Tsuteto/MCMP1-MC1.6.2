package tsuteto.mcmp.core.audio;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPool;

import org.apache.commons.io.FileUtils;

import paulscode.sound.SoundSystem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class SoundManagerAccessor
{
    private SoundManager sndManager;
    private final Class clsSoundManager = net.minecraft.client.audio.SoundManager.class;
    private final Method mtdRegisterSoundFile;

    public SoundManagerAccessor()
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        sndManager = mc.sndManager;

        mtdRegisterSoundFile = ReflectionHelper.findMethod(clsSoundManager, sndManager, new String[]{"a", "func_130081_a"}, File.class);
    }

    public File getAssetsDir()
    {
        return (File)ObfuscationReflectionHelper.getPrivateValue(clsSoundManager, sndManager, "i", "field_130085_i", "fileAssets");
    }

    public void registerWavFiles(String modSoundDir)
    {
        File soundDir = new File(getAssetsDir(), modSoundDir);
        if (soundDir.isDirectory())
        {
            Collection collection = FileUtils.listFiles(soundDir, new String[]{"wav"}, true);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext())
            {
                File file1 = (File)iterator.next();
                try
                {
                    this.mtdRegisterSoundFile.invoke(sndManager, file1);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Failed to register .wav files.", e);
                }
            }
        }
    }

    public SoundSystem sndSystem()
    {
        return sndManager.sndSystem;
    }

    public boolean loaded()
    {
        return (Boolean)ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, sndManager, "c", "loaded");
    }

    public SoundPool soundPoolStreaming()
    {
        return sndManager.soundPoolStreaming;
    }

    public SoundPool soundPoolSounds()
    {
        return sndManager.soundPoolSounds;
    }

    public int getTicksBeforeMusic()
    {
        return (Integer)ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, sndManager, "m", "ticksBeforeMusic");
    }

    public void setTicksBeforeMusic(int i)
    {
        ObfuscationReflectionHelper.setPrivateValue(SoundManager.class, sndManager, i, "m", "ticksBeforeMusic");
    }
}
