package de.ellpeck.thingsthatmatta.packet;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler{

    public static SimpleNetworkWrapper wrapper;

    public static void init(){
        wrapper = new SimpleNetworkWrapper(ThingsThatMatta.MOD_ID);

        wrapper.registerMessage(PacketSyncConfig.Handler.class, PacketSyncConfig.class, 0, Side.CLIENT);
    }

}
