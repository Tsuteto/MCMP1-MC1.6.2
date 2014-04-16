package tsuteto.mcmp.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class ResourceInstaller
{
    private File destDir;
    private List<Entry> srcList = new ArrayList<Entry>();
    private Class<?> srcClass;
    private boolean hasInstalled = false;
    
    public ResourceInstaller(File destDir)
    {
        this.destDir = destDir;
        this.srcClass = this.getClass();
    }

    public void addResource(String path, String filename)
    {
        URL url = srcClass.getResource(path);
        Entry entry = new Entry(url, filename);
        this.srcList.add(entry);
    }

    public void install()
    {
        for (Entry resource : srcList)
        {
            File destFile = new File(this.destDir, resource.filename);
            
            FileCopy fc = new FileCopy(resource.url, destFile);
            if (fc.verify()) continue;
            ModLog.log(Level.INFO, "Installing: %s", destFile.getPath());
            hasInstalled = true;

            fc.copy();
            if (!fc.verify())
            {
            	ModLog.log(Level.INFO, "Failed to install: %s", destFile.getPath());
                destFile.delete();
            }
        }
    }

    public void setResourceClass(Class<?> clazz)
    {
        this.srcClass = clazz;
    }
    
    public boolean hasInstalled()
    {
        return this.hasInstalled;
    }
    
    public class Entry
    {
        String filename;
        URL url;
        
        public Entry(URL url, String filename)
        {
            this.url = url;
            this.filename = filename;
        }
    }
}
