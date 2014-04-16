package tsuteto.mcmp.mcmp101;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import tsuteto.mcmp.core.McmpBaseMod;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = MCMP101.modId, name = "MCMP-101", version = "1.0.0-MC1.5")
public class MCMP101 extends McmpBaseMod
{
    public static final String modId = "mcmp101";
    public static int mcmp101Id = 7858;
    public static boolean useSmallFont = false;

    @Mod.Instance(MCMP101.modId)
    public static MCMP101 instance;

    public static Item itemMCMP101;

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event)
    {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();

            Property propEnabled = cfg.get("item", "mcmp101Id", mcmp101Id);
            mcmp101Id = propEnabled.getInt(mcmp101Id);

            Property propUseSmallFont = cfg.get("display", "useSmallFont", useSmallFont);
            useSmallFont = propEnabled.getBoolean(false);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "[MCMP-101] Failed to read cfg file");
        }
        finally
        {
            cfg.save();
        }

        itemMCMP101 = new ItemMCMP101(mcmp101Id)
                .setUnlocalizedName("mcmp101:mcmp101")
                .func_111206_d("mcmp101:mcmp101");

        ModLoader.addName(itemMCMP101, "MCMP-101");

        ModLoader.addRecipe(new ItemStack(itemMCMP101),
                new Object[] {
            "XX",
            "YY",
            Character.valueOf('X'), Block.obsidian,
            Character.valueOf('Y'), Item.redstoneRepeater
        });

        NetworkRegistry.instance().registerGuiHandler(this, new Mcmp101GuiHandler());
    }

    @Override
    public void resetPlayer(Minecraft minecraft)
    {
        ((ItemMCMP101) itemMCMP101).hasStartedUp = false;
    }
}
