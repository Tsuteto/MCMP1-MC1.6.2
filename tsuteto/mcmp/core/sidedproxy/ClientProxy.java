package tsuteto.mcmp.core.sidedproxy;

import net.minecraftforge.common.MinecraftForge;
import tsuteto.mcmp.core.McmpMain;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.eventhandler.CommonTickHandler;
import tsuteto.mcmp.core.eventhandler.SoundHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerComponents(McmpMain mod)
    {
        TickRegistry.registerTickHandler(new CommonTickHandler(), Side.CLIENT);
    }

    @Override
    public void installSoundFiles()
    {
        MinecraftForge.EVENT_BUS.register(new SoundHandler());
        McmpSoundManager.getInstance().registerWavFiles(McmpMain.songDir);

//        Minecraft mc = FMLClientHandler.instance().getClient();
//        File soundDir = new File(McmpSoundManager.getInstance().getAssetsDir(), "sound/MCMP1");
//        if (!soundDir.exists() && !soundDir.mkdirs())
//        {
//            ModLog.log(Level.WARNING, "Failed to make 'MCMP1' directory in " + soundDir.getAbsolutePath());
//            return;
//        }
//
//        ResourceInstaller installer = new ResourceInstaller(soundDir);
//        installer.addResource("/assets/mcmp1/sound/play.ogg", "play.ogg");
//        installer.addResource("/assets/mcmp1/sound/stop.ogg", "stop.ogg");
//        installer.install();
//
//        if (installer.hasInstalled())
//        {
//            ModLog.log(Level.INFO, "Done installing sound files");
//        }
    }
}
