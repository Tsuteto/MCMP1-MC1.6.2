package tsuteto.mcmp.core;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.changer.NetChannelChangerRename;
import tsuteto.mcmp.changer.NetChannelChangerState;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;
import tsuteto.mcmp.core.mcmpplayer.NetChannelMcmpPlayerCtl;
import tsuteto.mcmp.core.util.ModLog;
import tsuteto.mcmp.mcmp1.ItemMCMP1;
import tsuteto.mcmp.recorder.BlockRecorder;
import tsuteto.mcmp.recorder.NetChannelRecorderCtl;
import tsuteto.mcmp.recorder.TileEntityRecorder;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = McmpMain.modId, name = "MCMP-1", version = "1.2.5-MC1.6.2")
@NetworkMod(
        clientSideRequired = true, serverSideRequired = false,
        channels = {
                McmpMain.netChannelRecorderCtl,
                McmpMain.netChannelChangerState,
                McmpMain.netChannelChangerRename,
                McmpMain.netChannelMcmpPlayerCtl
        },
        packetHandler = McmpPacketHandler.class
        )
public class McmpMain extends McmpBaseMod
{
    public static final String modId = "mcmp1";
    public static final String netChannelRecorderCtl = "RecorderCtl";
    public static final String netChannelChangerState = "ChangerState";
    public static final String netChannelChangerRename = "ChangerRename";
    public static final String netChannelMcmpPlayerCtl = "PlayerCtl";

    public static final String songDir = "sound/MCMP1/songs";
    //public static final String songDir = "virtual/legacy/sound/MCMP1/songs";

    public static int mcmp1Id = 7855;
    public static int cassetteNormalId = 7856;
    public static int changerId = 7857;
    public static int recorderIdleId = 581;
    public static int recorderActiveId = 582;
    public static boolean useSmallFont = true;

    @Mod.Instance(modId)
    public static McmpMain instance;

    public static Item itemMCMP1;
    public static Item itemCassetteNormal;
    public static Item itemChanger;
    public static Block blockRecorderIdle;
    public static Block blockRecorderActive;

    public static boolean isDebug = false;

    public McmpSoundManager sndManager;

    static
    {
        ModLog.modId = "MCMP-1";
        ModLog.isDebug = isDebug;
    }

    public McmpMain()
    {
        sndManager = McmpSoundManager.getInstance();
    }

    @Mod.EventHandler
    public void earlyLoad(FMLPreInitializationEvent event)
    {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();

            mcmp1Id = cfg.get("item", "mcmp1Id", mcmp1Id).getInt(mcmp1Id);
            cassetteNormalId = cfg.get("item", "cassetteNormalId", cassetteNormalId).getInt(cassetteNormalId);
            changerId = cfg.get("item", "changerId", changerId).getInt(changerId);
            recorderIdleId = cfg.get("block", "recorderIdleId", recorderIdleId).getInt(recorderIdleId);
            recorderActiveId = cfg.get("block", "recorderActiveId", recorderActiveId).getInt(recorderActiveId);
            useSmallFont = cfg.get("gui", "useSmallFont", useSmallFont).getBoolean(false);
        }
        catch (Exception e)
        {
            ModLog.log(Level.SEVERE, e, "Failed to read cfg file");
        }
        finally
        {
            cfg.save();
        }

        /*
         * Define items, blocks
         */
        itemMCMP1 = new ItemMCMP1(mcmp1Id)
                .setUnlocalizedName("mcmp1:mcmp1")
                .func_111206_d("mcmp1:mcmp1");

        itemCassetteNormal = new ItemCassetteTape(cassetteNormalId)
                .setUnlocalizedName("mcmp1:cassetteNormal")
                .func_111206_d("mcmp1:cassetteNormal")
                .setCreativeTab(CreativeTabs.tabMisc);

        itemChanger = new ItemChanger(changerId)
                .setUnlocalizedName("mcmp1:cassetteChanger")
                .func_111206_d("mcmp1:cassetteChanger")
                .setCreativeTab(CreativeTabs.tabTools);

        blockRecorderIdle = new BlockRecorder(recorderIdleId, false)
                .setHardness(3.5F)
                .setStepSound(Block.soundMetalFootstep)
                .setUnlocalizedName("mcmp1:dubbingMachine")
                .func_111022_d("mcmp1:dubbingMachine")
                .setCreativeTab(CreativeTabs.tabDecorations);

        blockRecorderActive = new BlockRecorder(recorderActiveId, true)
                .setHardness(3.5F)
                .setStepSound(Block.soundMetalFootstep)
                .setUnlocalizedName("mcmp1:dubbingMachine")
                .func_111022_d("mcmp1:dubbingMachine")
                .setLightValue(0.75F)
                .setCreativeTab(CreativeTabs.tabDecorations);

        /*
         * Register blocks
         */
        GameRegistry.registerBlock(blockRecorderIdle, "McmpRecorder");
        GameRegistry.registerTileEntity(TileEntityRecorder.class, "McmpRecorder");

        /*
         * Sound file installation
         */
        sidedProxy.installSoundFiles();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        /*
         * Register sided components
         */
        sidedProxy.registerComponents(this);

        /*
         * Register GUI
         */
        NetworkRegistry.instance().registerGuiHandler(this, new McmpGuiHandler());

        /*
         * Register network channel handlers
         */
        McmpPacketHandler.handlerRegistry.put(McmpMain.netChannelRecorderCtl, new NetChannelRecorderCtl());
        McmpPacketHandler.handlerRegistry.put(McmpMain.netChannelChangerState, new NetChannelChangerState());
        McmpPacketHandler.handlerRegistry.put(McmpMain.netChannelChangerRename, new NetChannelChangerRename());
        McmpPacketHandler.handlerRegistry.put(McmpMain.netChannelMcmpPlayerCtl, new NetChannelMcmpPlayerCtl());

        /*
         * Entry item names
         */
        ModLoader.addName(itemMCMP1, "MCMP-1");
        ModLoader.addName(itemChanger, "en_US", "Cassette Changer");
        ModLoader.addName(itemChanger, "ja_JP", "カセットチェンジャー");
        ModLoader.addName(blockRecorderIdle, "en_US", "Cassette Recorder");
        ModLoader.addName(blockRecorderIdle, "ja_JP", "カセットレコーダー");

        String[] colorNamesEn = new String[] { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Silver", "Gray", "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "" };
        String[] colorNamesJp = new String[] { "ブラック", "レッド", "グリーン", "ブラウン", "ブルー", "パープル", "シアン", "シルバー", "グレー", "ピンク", "ライム", "イエロー", "ライトブルー", "マゼンタ", "オレンジ", "" };
        for (int i = 0; i < 15; i++)
        {
            ModLoader.addName(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDyeTypeFromDamage(i)),
                    "en_US", "Cassette Tape " + colorNamesEn[i]);
            ModLoader.addName(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDyeTypeFromDamage(i)),
                    "ja_JP", "カセットテープ " + colorNamesJp[i]);
        }
        ModLoader.addName(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDyeTypeFromDamage(15)),
                "en_US", "Cassette Tape");
        ModLoader.addName(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDyeTypeFromDamage(15)),
                "ja_JP", "カセットテープ");

        ModLoader.addLocalization("container.mcmp1.Recorder", "en_US", "Cassette Recorder");
        ModLoader.addLocalization("container.mcmp1.Recorder", "ja_JP", "カセットレコーダー");

        ModLoader.addLocalization("container.mcmp1.Changer", "en_US", "Cassette Changer");
        ModLoader.addLocalization("container.mcmp1.Changer", "ja_JP", "カセットチェンジャー");

        ModLoader.addLocalization("mcmp1.playing", "Playing: %s");

        ModLoader.addLocalization("mcmp1.fileNotFound", "en_US", "*Not found* %s");
        ModLoader.addLocalization("mcmp1.fileNotFound", "ja_JP", "*見つかりません* %s");
        ModLoader.addLocalization("mcmp1.recordNotFound", "en_US", "*Not found* %s");
        ModLoader.addLocalization("mcmp1.recordNotFound", "ja_JP", "*存在しません* %s");

        ModLoader.addLocalization("mcmp1.numSongs", "en_US", "Contains %d %s");
        ModLoader.addLocalization("mcmp1.numSongs", "ja_JP", "%d%s入り");
        ModLoader.addLocalization("mcmp1.song", "en_US", "song");
        ModLoader.addLocalization("mcmp1.songs", "en_US", "songs");
        ModLoader.addLocalization("mcmp1.song", "ja_JP", "曲");
        ModLoader.addLocalization("mcmp1.songs", "ja_JP", "曲");

        ModLoader.addLocalization("mcmp1.changerName.label", "en_US", "Label");
        ModLoader.addLocalization("mcmp1.changerName.label", "ja_JP", "ラベル");

        /*
         * Recipes
         */

        // MCMP-1
        ModLoader.addRecipe(new ItemStack(itemMCMP1),
                new Object[] {
            "XX",
            "YY",
            Character.valueOf('X'), Item.ingotIron,
            Character.valueOf('Y'), Item.redstoneRepeater,
        });

        // Cassette Tape
        ModLoader.addRecipe(new ItemStack(itemCassetteNormal, 4),
                new Object[] {
            "XX",
            "YY",
            Character.valueOf('X'), Item.silk,
            Character.valueOf('Y'), Block.planks,
        });

        // Recorder
        ModLoader.addRecipe(new ItemStack(blockRecorderIdle),
                new Object[] {
            " X ",
            "X*X",
            "---",
            Character.valueOf('X'), Item.ingotIron,
            Character.valueOf('*'), Item.diamond,
            Character.valueOf('-'), Item.redstoneRepeater,
        });

        // Cassette Changer
        ModLoader.addRecipe(new ItemStack(itemChanger),
                new Object[] {
            "XXX",
            "Y Y",
            "YYY",
            Character.valueOf('X'), Item.ingotIron,
            Character.valueOf('Y'), Block.planks,
        });

        // Color variation of cassette tapes
        for (int i = 0; i < 16; i++)
        {
            ModLoader.addShapelessRecipe(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDamageFromDyeType(i)),
                    new Object[] {
                itemCassetteNormal,
                new ItemStack(Item.dyePowder, 1, i),
            });
            ModLoader.addRecipe(new ItemStack(itemCassetteNormal, 4, ItemCassetteTape.getDamageFromDyeType(i)),
                    new Object[] {
                " XX",
                "DYY",
                Character.valueOf('X'), Item.silk,
                Character.valueOf('Y'), Block.planks,
                Character.valueOf('D'), new ItemStack(Item.dyePowder, 1, i),
            });
        }

        // For making cassettes blank
        ModLoader.addShapelessRecipe(new ItemStack(itemCassetteNormal),
                new Object[] {
            new ItemStack(itemCassetteNormal, 1, 0x7FFF),
            new ItemStack(Item.dyePowder, 1, 15),
        });
    }

    @Mod.EventHandler
    public void onModsLoaded(FMLPostInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        this.sndManager.stop();
    }

    @Override
    public boolean onTickInGUI(float f, Minecraft minecraft, GuiScreen guiscreen)
    {
        if (guiscreen != null)
        {
            if (guiscreen instanceof GuiMainMenu && sndManager.playing())
            {
            }
            else
            {
                for (ItemMcmpPlayer mcmpPlayer : McmpPlayerManager.getPlayerList())
                {
                    mcmpPlayer.inInventory = true;
                }
            }
        }
        return true;
    }
}
