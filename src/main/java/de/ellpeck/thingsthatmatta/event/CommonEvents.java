package de.ellpeck.thingsthatmatta.event;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import net.minecraft.entity.player.EntityPlayer;
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
        if(ThingsThatMatta.shouldResetPlayerSpawns){
            EntityPlayer player = event.player;
            if(player != null && !player.world.isRemote){
                int dim = player.world.provider.getDimension();
                if(!player.isSpawnForced(dim) && player.getBedLocation(dim) != null){
                    player.setSpawnPoint(null, false);
                }
            }
        }
    }

}
