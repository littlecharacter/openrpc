package com.lc.rpc.remoting.impl;

import com.alibaba.fastjson.JSON;
import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.protocol.ResponseBody;
import com.lc.rpc.remoting.callback.ClientCallback;
import com.lc.rpc.remoting.decoder.MsgDecoder;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class NettyClient extends AbstractServer {
    private SocketChannel channel;
    private CountDownLatch latch;

    public NettyClient(InetSocketAddress address, CountDownLatch latch) {
        super(address);
        this.latch = latch;
        startServer();
    }

    @Override
    public void startServer() {
        new Thread(() -> {
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
                System.out.println("连接成功");
                latch.countDown();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 释放资源
                workerGroup.shutdownGracefully();
            }
            System.out.println("通道关闭!");
        }).start();
    }

    @Override
    public void sendMsg(Message<?> message) {
        MsgHead msgHead = message.getMsgHead();
        RequestBody requestBody = (RequestBody) message.getMsgBody();
        ObjectSerializer serializer = new KryoSerializer();
        byte[] bodyBytes = serializer.serialize(requestBody);
        msgHead.setDataLength(bodyBytes.length);
        try {
            channel.writeAndFlush(Unpooled.wrappedBuffer(msgHead.getHead(), bodyBytes)).sync();
        } catch (Exception e) {
            throw new RuntimeException("通道（" + channel.remoteAddress().getHostString() + "）异常：" + e.getMessage());
        }
    }

    private static class ReceiveHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Message<?> message = (Message<?>) msg;
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            System.out.println("Client：收到Server（" + remoteAddress.toString() + "）的回复 - " + JSON.toJSONString(message));
            ClientCallback.runCallback(message);
        }

    }
}
