package de.ellpeck.thingsthatmatta;

import de.ellpeck.thingsthatmatta.event.CommonEvents;
import de.ellpeck.thingsthatmatta.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ThingsThatMatta.MOD_ID, name = ThingsThatMatta.NAME, version = ThingsThatMatta.VERSION, guiFactory = ThingsThatMatta.PROXY_PATH+"GuiFactory")
public class ThingsThatMatta{

    public static final String MOD_ID = "thingsthatmatta";
    public static final String NAME = "ThingsThatMatta";
    public static final String VERSION = "@VERSION@";
    public static final String PROXY_PATH = "de.ellpeck.thingsthatmatta.proxy.";

    public static Configuration config;

    @SidedProxy(clientSide = PROXY_PATH+"ClientProxy", serverSide = PROXY_PATH+"ServerProxy")
    public static IProxy proxy;

    private static final String[] DEFAULT_DEBUG_HIDE_KEYS = new String[]{"MultiplayerChunkCache:", "XYZ:", "Block:", "Chunk:", "Facing:", "Biome:", "Light:", "Local Difficulty:", "Looking at:"};
    public static String[] debugHideKeys;

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

        if(config.hasChanged()){
            config.save();
        }
    }
}
