package de.ellpeck.thingsthatmatta.event;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents{

    @SubscribeEvent
    public void onDebugScreen(RenderGameOverlayEvent.Text event){
        List<String> left = event.getLeft();

        for(int i = 0; i < left.size(); i++){
            String s = left.get(i);
            if(s != null && !s.isEmpty()){
                for(String start : ThingsThatMatta.debugHideKeys){
                    if(s.startsWith(start)){
                        left.remove(i);
                        i--;

                        break;
                    }
                }
            }
        }
    }

}
