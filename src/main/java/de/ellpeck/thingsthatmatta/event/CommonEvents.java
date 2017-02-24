package de.ellpeck.thingsthatmatta.event;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import de.ellpeck.thingsthatmatta.packet.PacketHandler;
import de.ellpeck.thingsthatmatta.packet.PacketSyncConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.Arrays;
import java.util.Random;

public class CommonEvents{

    public static final DataParameter<BlockPos> PLAYER_BED_POS = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.BLOCK_POS);

    @SubscribeEvent
    public void onConfigurationChangedEvent(OnConfigChangedEvent event){
        if(ThingsThatMatta.MOD_ID.equals(event.getModID())){
            ThingsThatMatta.defineConfigs();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(player != null && !player.world.isRemote){
            if(ThingsThatMatta.shouldResetPlayerSpawns){
                int dim = player.world.provider.getDimension();
                if(!player.isSpawnForced(dim) && player.getBedLocation(dim) != null){
                    player.setSpawnPoint(null, false);
                }
            }

            String tagName = ThingsThatMatta.MOD_ID+"StartSleepTime";
            NBTTagCompound data = player.getEntityData();

            int currTime = (int)player.world.getWorldTime();
            boolean isSleeping = player.isPlayerSleeping();
            int startSleepTime = data.getInteger(tagName);

            if(startSleepTime <= 0 && isSleeping){
                data.setInteger(tagName, currTime);

                if(ThingsThatMatta.compassPointsToBedSpawn){
                    if(player.bedLocation != null){
                        player.getDataManager().set(PLAYER_BED_POS, player.bedLocation);
                    }
                }
            }
            else if(startSleepTime > 0 && !isSleeping){
                if(ThingsThatMatta.sleepHealAmountPerTick > 0F){
                    float sleepAmount = currTime-startSleepTime;
                    float healAmount = ThingsThatMatta.sleepHealAmountPerTick*sleepAmount;
                    if(healAmount > 0){
                        player.heal(healAmount);
                    }
                }

                data.removeTag(tagName);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event){
        EntityLivingBase living = event.getEntityLiving();
        World world = living.getEntityWorld();

        if(!world.isRemote && living instanceof EntityPlayer){
            moveSpawnPoint(world);
        }
    }

    @SubscribeEvent
    public void onEntityInit(EntityConstructing event){
        if(ThingsThatMatta.compassPointsToBedSpawn){
            Entity entity = event.getEntity();
            if(entity instanceof EntityPlayer){
                entity.getDataManager().register(PLAYER_BED_POS, BlockPos.ORIGIN);
            }
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerLoggedInEvent event){
        if(ThingsThatMatta.syncConfigToClients){
            EntityPlayer player = event.player;
            if(player instanceof EntityPlayerMP && !player.world.isRemote){
                PacketSyncConfig packet = new PacketSyncConfig(ThingsThatMatta.debugHideKeys);
                PacketHandler.wrapper.sendTo(packet, (EntityPlayerMP)player);

                ThingsThatMatta.LOGGER.info("Sending debugHideKeys config "+Arrays.toString(ThingsThatMatta.debugHideKeys)+" to player "+player.getName()+".");
            }
        }
    }

    private static void moveSpawnPoint(World world){
        if(ThingsThatMatta.spawnResetRange > 0){
            BiomeProvider provider = world.provider.getBiomeProvider();
            Random rand = new Random(world.getSeed());

            BlockPos oldSpawn = world.getSpawnPoint();
            BlockPos pos = provider.findBiomePosition(oldSpawn.getX(), oldSpawn.getZ(), ThingsThatMatta.spawnResetRange, provider.getBiomesToSpawnIn(), rand);

            int x;
            int z;

            if(pos != null){
                x = pos.getX();
                z = pos.getZ();
            }
            else{
                x = oldSpawn.getX()+MathHelper.getInt(rand, -ThingsThatMatta.spawnResetRange, ThingsThatMatta.spawnResetRange);
                z = oldSpawn.getZ()+MathHelper.getInt(rand, -ThingsThatMatta.spawnResetRange, ThingsThatMatta.spawnResetRange);
            }

            for(int i = 0; i < 1000; i++){
                if(!world.provider.canCoordinateBeSpawn(x, z)){
                    x += rand.nextInt(64)-rand.nextInt(64);
                    z += rand.nextInt(64)-rand.nextInt(64);
                }
                else{
                    break;
                }
            }

            BlockPos newSpawn = new BlockPos(x, world.provider.getAverageGroundLevel(), z);
            world.getWorldInfo().setSpawn(newSpawn);

            ThingsThatMatta.LOGGER.info("Moved Spawn Point from {}, {}, {} to {}, {}, {}.", oldSpawn.getX(), oldSpawn.getY(), oldSpawn.getZ(), newSpawn.getX(), newSpawn.getY(), newSpawn.getZ());
        }
    }
}
