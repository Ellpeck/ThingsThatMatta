package de.ellpeck.thingsthatmatta.proxy;

import de.ellpeck.thingsthatmatta.event.ClientEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy{

    @Override
    public void preInit(FMLPreInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
