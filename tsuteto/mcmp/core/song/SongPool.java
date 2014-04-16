package tsuteto.mcmp.core.song;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tsuteto.mcmp.core.McmpMain;
import tsuteto.mcmp.core.audio.EnumSoundSystemType;
import tsuteto.mcmp.core.util.ModLog;
import cpw.mods.fml.common.FMLLog;

public class SongPool
{
    public Map<String, SongInfo> songMap = new HashMap<String, SongInfo>();
    public List<SongInfo> songList = new ArrayList<SongInfo>();

    public SongPool(File assetsDir)
    {
        File songdir = new File(assetsDir, McmpMain.songDir);
        ModLog.debug("Song dir: %s", songdir.getPath());
        if (!songdir.exists() && !songdir.mkdirs())
            return;

        File[] songFiles = songdir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                if (!new File(dir, name).isFile())
                    return false;

                int dotpos = name.lastIndexOf(".");

                if (dotpos <= 0)
                    return false;

                String ext = name.substring(dotpos + 1);

                return "mp3".equalsIgnoreCase(ext)
                        || "ogg".equalsIgnoreCase(ext)
                        || "wav".equalsIgnoreCase(ext)
                        || "mus".equalsIgnoreCase(ext);
            }
        });

        for (File file : songFiles)
        {
            String filename = file.getName();
            SongInfo info = new SongInfo();

            info.file = file;
            info.songName = filename.substring(0, filename.lastIndexOf("."));

            if (filename.toLowerCase().lastIndexOf(".mp3") + 4 == filename.length())
            {
                info.playerType = EnumSoundSystemType.MP3;
            }
            else
            {
                info.playerType = EnumSoundSystemType.INTERNAL;
            }
            songMap.put(info.file.getName(), info);
            songList.add(info);
            FMLLog.fine("[MCMP-1] Loaded song file: " + filename);
        }
    }
}
