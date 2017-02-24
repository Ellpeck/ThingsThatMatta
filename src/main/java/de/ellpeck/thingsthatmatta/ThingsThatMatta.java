package de.ellpeck.thingsthatmatta;

import de.ellpeck.thingsthatmatta.event.CommonEvents;
import de.ellpeck.thingsthatmatta.proxy.IProxy;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    public static boolean compassPointsToBedSpawn;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        defineConfigs();

        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        proxy.preInit(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        if(compassPointsToBedSpawn){
            screwWithCompass();
        }
    }

    public static void defineConfigs(){
        debugHideKeys = config.getStringList("debugHideKeys", Configuration.CATEGORY_GENERAL, DEFAULT_DEBUG_HIDE_KEYS, "A list of things that text in the F3 debug menu should start with so that it gets hidden from it");
        shouldResetPlayerSpawns = config.getBoolean("unsetPlayerSpawns", Configuration.CATEGORY_GENERAL, true, "If custom spawn points set by players (ie sleeping in beds) should be removed and setting new ones should be hindered");
        sleepHealAmountPerTick = config.getFloat("sleepHealAmount", Configuration.CATEGORY_GENERAL, 0.01F, 0F, 100F, "The amount of health points that is regenerated when a player sleeps in bed per tick, this is tracked based on the world time the player goes to bed until the world time the player wakes up at. Set to 0 to disable");
        spawnResetRange = config.getInt("spawnResetRange", Configuration.CATEGORY_GENERAL, 2000, 0, 1000000, "The amount of blocks that spawn gets moved every time a player dies. Set to 0 to disable");
        compassPointsToBedSpawn = config.getBoolean("compassPointsToBedSpawn", Configuration.CATEGORY_GENERAL, true, "If the compass should point to the last bed that you slept in instead of the world spawn");

        if(config.hasChanged()){
            config.save();
        }
    }

    private static void screwWithCompass(){
        //Copied from ItemCompass, getSpawnToAngle changed
        Items.COMPASS.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter(){
            @SideOnly(Side.CLIENT)
            double rotation;
            @SideOnly(Side.CLIENT)
            double rota;
            @SideOnly(Side.CLIENT)
            long lastUpdateTick;

            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World world, EntityLivingBase entity){
                if(entity == null && !stack.isOnItemFrame()){
                    return 0.0F;
                }
                else{
                    boolean flag = entity != null;
                    Entity theEntity = flag ? entity : stack.getItemFrame();

                    if(world == null){
                        world = theEntity.world;
                    }

                    double d0;
                    if(world.provider.isSurfaceWorld()){
                        double d1 = flag ? (double)theEntity.rotationYaw : this.getFrameRotation((EntityItemFrame)theEntity);
                        d1 = d1%360.0D;
                        double d2 = this.getSpawnToAngle(world, theEntity);
                        d0 = Math.PI-((d1-90.0D)*0.01745329238474369D-d2);
                    }
                    else{
                        d0 = Math.random()*(Math.PI*2D);
                    }

                    if(flag){
                        d0 = this.wobble(world, d0);
                    }

                    float f = (float)(d0/(Math.PI*2D));
                    return MathHelper.positiveModulo(f, 1.0F);
                }
            }

            @SideOnly(Side.CLIENT)
            private double wobble(World worldIn, double d){
                if(worldIn.getTotalWorldTime() != this.lastUpdateTick){
                    this.lastUpdateTick = worldIn.getTotalWorldTime();
                    double d0 = d-this.rotation;
                    d0 = d0%(Math.PI*2D);
                    d0 = MathHelper.clamp(d0, -1.0D, 1.0D);
                    this.rota += d0*0.1D;
                    this.rota *= 0.8D;
                    this.rotation += this.rota;
                }

                return this.rotation;
            }

            @SideOnly(Side.CLIENT)
            private double getFrameRotation(EntityItemFrame frame){
                return (double)MathHelper.clampAngle(180+frame.facingDirection.getHorizontalIndex()*90);
            }

            @SideOnly(Side.CLIENT)
            private double getSpawnToAngle(World world, Entity entity){
                BlockPos pos = null;

                if(entity instanceof EntityPlayer){
                    BlockPos bedPos = entity.getDataManager().get(CommonEvents.PLAYER_BED_POS);
                    if(world.isBlockLoaded(bedPos, false)){
                        IBlockState state = world.getBlockState(bedPos);
                        if(state.getBlock() instanceof BlockBed){
                            pos = bedPos;
                        }
                    }
                    else{
                        pos = bedPos;
                    }
                }

                if(pos == null){
                    pos = world.getSpawnPoint();
                }

                return Math.atan2((double)pos.getZ()-entity.posZ, (double)pos.getX()-entity.posX);
            }
        });
    }
}
