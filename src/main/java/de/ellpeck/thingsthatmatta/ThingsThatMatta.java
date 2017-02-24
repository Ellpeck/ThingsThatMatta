package de.ellpeck.thingsthatmatta;

import de.ellpeck.thingsthatmatta.event.CommonEvents;
import de.ellpeck.thingsthatmatta.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ThingsThatMatta.MOD_ID, name = ThingsThatMatta.NAME, version = ThingsThatMatta.VERSION, guiFactory = ThingsThatMatta.PROXY_PATH+"GuiFactory")
public class ThingsThatMatta{

    public static final String MOD_ID = "thingsthatmatta";
    public static final String NAME = "ThingsThatMatta";
    public static final String VERSION = "@VERSION@";
    public static final String PROXY_PATH = "de.ellpeck.thingsthatmatta.proxy.";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static Configuration config;

    @SidedProxy(clientSide = PROXY_PATH+"ClientProxy", serverSide = PROXY_PATH+"ServerProxy")
    public static IProxy proxy;

    private static final String[] DEFAULT_DEBUG_HIDE_KEYS = new String[]{"MultiplayerChunkCache:", "XYZ:", "Block:", "Chunk:", "Facing:", "Biome:", "Light:", "Local Difficulty:", "Looking at:"};
    public static String[] debugHideKeys;
    public static boolean shouldResetPlayerSpawns;
    public static float sleepHealAmountPerTick;
    public static int spawnResetRange;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        defineConfigs();

        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        proxy.preInit(event);
    }

    public static void defineConfigs(){
        debugHideKeys = config.getStringList("debugHideKeys", Configuration.CATEGORY_GENERAL, DEFAULT_DEBUG_HIDE_KEYS, "A list of things that text in the F3 debug menu should start with so that it gets hidden from it");
        shouldResetPlayerSpawns = config.getBoolean("unsetPlayerSpawns", Configuration.CATEGORY_GENERAL, true, "If custom spawn points set by players (ie sleeping in beds) should be removed and setting new ones should be hindered");
        sleepHealAmountPerTick = config.getFloat("sleepHealAmount", Configuration.CATEGORY_GENERAL, 0.01F, 0F, 100F, "The amount of health points that is regenerated when a player sleeps in bed per tick, this is tracked based on the world time the player goes to bed until the world time the player wakes up at. Set to 0 to disable");
        spawnResetRange = config.getInt("spawnResetRange", Configuration.CATEGORY_GENERAL, 2000, 0, 1000000, "The amount of blocks that spawn gets moved every time a player dies. Set to 0 to disable");

        if(config.hasChanged()){
            config.save();
        }
    }
}
