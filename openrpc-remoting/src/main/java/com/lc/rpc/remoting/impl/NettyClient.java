package com.lc.rpc.remoting.impl;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.ResponseBody;
import com.lc.rpc.remoting.decoder.MsgDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class NettyClient extends AbstractServer {
    private SocketChannel channel;

    public NettyClient(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void startServer() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) {
                ChannelPipeline pipeline = sc.pipeline();
                pipeline.addLast(new MsgDecoder<ResponseBody>());
                pipeline.addLast(new ReceiveHandler());
            }
        });
        try {
            channel = (NioSocketChannel) bootstrap.connect(address).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            // 释放资源
            workerGroup.shutdownGracefully();
        }
        System.out.println("通道关闭!");
    }

    @Override
    public void sendMsg(ByteBuf msgBuf) {
        channel.writeAndFlush(msgBuf);
    }

    private static class ReceiveHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Message<ResponseBody> message = (Message<ResponseBody>) msg;
            super.channelRead(ctx, msg);
        }

    }
}
