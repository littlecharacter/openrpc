package com.lc.rpc.remoting.impl;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.protocol.ResponseBody;
import com.lc.rpc.remoting.callback.ServerCallback;
import com.lc.rpc.remoting.decoder.MsgDecoder;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class NettyServer extends AbstractServer {
    private static ServerCallback serverCallback;

    public NettyServer(InetSocketAddress address, ServerCallback serverCallback) {
        super(address);
        startServer();
        NettyServer.serverCallback = serverCallback;
    }

    @Override
    public void startServer() {
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture bind = bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) {
                            ChannelPipeline pipeline = sc.pipeline();
                            pipeline.addLast(new MsgDecoder<RequestBody>());
                            pipeline.addLast(new ReceiveHandler());
                        }
                    }).bind(address.getPort());
            try {
                bind.sync().channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                // 释放资源
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
            System.out.println("通道关闭!");
        }).start();
    }

    private static class ReceiveHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Message<?> message = (Message<?>) msg;
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            System.out.println("Server：收到Client（" + remoteAddress.toString() + "）的消息 - " + JSON.toJSONString(message));
            // 0，直接这当前方法处理业务
            Message<?> result = serverCallback.call(message);
            MsgHead msgHead = result.getMsgHead();
            ResponseBody responseBody = (ResponseBody) result.getMsgBody();
            ObjectSerializer serializer = new KryoSerializer();
            byte[] responseBytes = serializer.serialize(responseBody);
            msgHead.setFlag((byte) -1);
            msgHead.setDataLength(responseBytes.length);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(msgHead.getHead(), responseBytes));

            // 1，用当前的 EventLoop 来处理业务
            // ctx.executor().execute(() -> {
            //     Message result = serverCallback.call(message);
            //     MsgHead msgHead = result.getMsgHead();
            //     ResponseBody responseBody = result.getResponseBody();
            //     ObjectSerializer serializer = new KryoSerializer();
            //     byte[] responseBytes = serializer.serialize(responseBody);
            //     msgHead.setFlag((byte) -1);
            //     msgHead.setDataLength(responseBytes.length);
            //     ctx.writeAndFlush(Unpooled.wrappedBuffer(msgHead.getHead(), responseBytes));
            // });

            // 2，用当前组的其他 EventLoop 来处理业务
            // ctx.executor().parent().next().execute(() -> {
            //     Message result = serverCallback.call(message);
            //     MsgHead msgHead = result.getMsgHead();
            //     ResponseBody responseBody = result.getResponseBody();
            //     ObjectSerializer serializer = new KryoSerializer();
            //     byte[] responseBytes = serializer.serialize(responseBody);
            //     msgHead.setFlag((byte) -1);
            //     msgHead.setDataLength(responseBytes.length);
            //     ctx.writeAndFlush(Unpooled.wrappedBuffer(msgHead.getHead(), responseBytes));
            // });

            // 3，自己创建线程池执行业务
        }
    }
}