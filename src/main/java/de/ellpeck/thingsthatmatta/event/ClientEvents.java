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
        for(int side = 0; side < 2; side++){
            List<String> list = side == 0 ? event.getLeft() : event.getRight();
            for(int i = 0; i < list.size(); i++){
                String s = list.get(i);
                if(s != null && !s.isEmpty()){
                    for(String start : ThingsThatMatta.debugHideKeys){
                        if(s.startsWith(start)){
                            list.remove(i);
                            i--;

                            break;
                        }
                    }
                }
            }
        }
    }

}
