package de.ellpeck.thingsthatmatta.event;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEvents{

    @SubscribeEvent
    public void onConfigurationChangedEvent(OnConfigChangedEvent event){
        if(ThingsThatMatta.MOD_ID.equals(event.getModID())){
            ThingsThatMatta.defineConfigs();
        }
    }

}
