/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.tirelesstraveler.fancywarpmenu.listeners;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
public class SkyBlockJoinListener extends SimpleChannelInboundHandler<S3DPacketDisplayScoreboard> {
    private static final String SERVER_BRAND_START = "Hypixel BungeeCord";

    private static final Logger logger = LogManager.getLogger();
    private Channel channel;
    private final WarpMenuListener warpMenuListener;
    private boolean serverBrandChecked;
    private boolean onHypixel;
    private boolean onSkyBlock;

    public SkyBlockJoinListener(WarpMenuListener warpMenuListener) {
        super(false);
        this.warpMenuListener = warpMenuListener;
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (!e.isLocal && e.connectionType.equals("MODDED")) {
            channel = e.manager.channel();
            // Insert before vanilla's packet handler.
            channel.pipeline().addBefore("packet_handler", "skyblock_join_listener", this);
            logger.debug("Connected to a server.");
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if (onHypixel) {
            serverBrandChecked = false;
            onHypixel = false;
            onSkyBlock = false;
            channel = null;
            logger.debug("Disconnected from Hypixel.");
            try {
                channel.pipeline().remove("skyblock_join_listener");
            } catch (Exception ignored) {
                // It throws when the handler doesn't exist, which is fine in this case
            }
        }
    }

    /**
     * This method listens for {@link S3DPacketDisplayScoreboard} packets. These are sent once per world join/switch
     * and contain the name of the game mode, thus making them excellent for checking whether the player is on Skyblock.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, S3DPacketDisplayScoreboard packet) {
        if (channelHandlerContext.channel().isOpen()) {
            try {
                if (!serverBrandChecked) {
                    EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
                    onHypixel = thePlayer.getClientBrand().startsWith(SERVER_BRAND_START);
                    serverBrandChecked = true;

                    if (onHypixel) {
                        logger.info("Player joined Hypixel.");
                    } else {
                        try {
                            channel.pipeline().remove("skyblock_join_listener");
                            channel.pipeline().remove("warp_menu_blocker");
                        } catch (Exception ignored) {
                            // It throws when the handler doesn't exist, which is fine in this case
                        }
                    }
                }

                // 1 is the sidebar objective slot.
                if (onHypixel && packet.func_149371_c() == 1) {
                     String objectiveName = packet.func_149370_d();
                     boolean newSkyBlockState = objectiveName.equals("SBScoreboard");

                    if (newSkyBlockState && !onSkyBlock) {
                        logger.info("Player joined SkyBlock.");
                    } else if (!newSkyBlockState && onSkyBlock) {
                        logger.info("Player left SkyBlock.");
                    }

                    onSkyBlock = newSkyBlockState;
                }
            } catch (ThreadQuickExitException e) {
                //ignored
            }
        }

        channelHandlerContext.fireChannelRead(packet);
    }

    public boolean isOnSkyBlock() {
        return onSkyBlock;
    }
}