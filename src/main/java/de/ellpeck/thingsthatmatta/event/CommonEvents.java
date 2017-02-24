package de.ellpeck.thingsthatmatta.event;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CommonEvents{

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

            if(ThingsThatMatta.sleepHealAmountPerTick > 0F){
                NBTTagCompound data = player.getEntityData();

                int currTime = (int)player.world.getWorldTime();
                boolean isSleeping = player.isPlayerSleeping();
                int startSleepTime = data.getInteger("StartSleepTime");

                if(startSleepTime <= 0 && isSleeping){
                    data.setInteger("StartSleepTime", currTime);
                }
                else if(startSleepTime > 0 && !isSleeping){
                    float sleepAmount = currTime-startSleepTime;
                    float healAmount = ThingsThatMatta.sleepHealAmountPerTick*sleepAmount;
                    if(healAmount > 0){
                        player.heal(healAmount);
                    }

                    data.removeTag("StartSleepTime");
                }
            }
        }
    }

}
