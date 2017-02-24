package de.ellpeck.thingsthatmatta.packet;

import de.ellpeck.thingsthatmatta.ThingsThatMatta;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

public class PacketSyncConfig implements IMessage{

    private String[] debugMenuConfig;

    public PacketSyncConfig(){

    }

    public PacketSyncConfig(String[] debugMenuConfig){
        this.debugMenuConfig = debugMenuConfig;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        this.debugMenuConfig = new String[buffer.readInt()];

        for(int i = 0; i < this.debugMenuConfig.length; i++){
            this.debugMenuConfig[i] = buffer.readString(64);
        }
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeInt(this.debugMenuConfig.length);

        for(String s : this.debugMenuConfig){
            buffer.writeString(s);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncConfig, IMessage>{

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(final PacketSyncConfig message, MessageContext ctx){
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){
                @Override
                public void run(){
                    ThingsThatMatta.debugHideKeys = message.debugMenuConfig;
                    ThingsThatMatta.LOGGER.info("Overriding debugHideKeys config "+Arrays.toString(ThingsThatMatta.debugHideKeys)+" with "+Arrays.toString(message.debugMenuConfig)+" for this server.");
                }
            });
            return null;
        }
    }
}
