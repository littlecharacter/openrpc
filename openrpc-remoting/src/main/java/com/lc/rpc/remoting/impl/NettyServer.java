package com.lc.rpc.remoting.impl;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.remoting.callback.ServerCallback;
import com.lc.rpc.remoting.decoder.MsgDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class NettyServer extends AbstractServer {
    private static ServerCallback serverCallback;

    public NettyServer(InetSocketAddress address, ServerCallback serverCallback) {
        super(address);
        this.serverCallback = serverCallback;
    }

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) {
                ChannelPipeline pipeline = sc.pipeline();
                pipeline.addLast(new MsgDecoder<RequestBody>());
                pipeline.addLast(new ReceiveHandler());
            }
        });
        try {
            ChannelFuture f = bootstrap.bind(8765).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            // 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        System.out.println("通道关闭!");
    }

    private static class ReceiveHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Message<RequestBody> message = (Message<RequestBody>) msg;
            // 0，直接这当前方法处理业务
            ByteBuf resultBuf = serverCallback.call(message);
            ctx.writeAndFlush(resultBuf);

            // 1，用当前的 EventLoop 来处理业务
            // ctx.executor().execute(() -> {
            //     ByteBuf resultBuf = callBack.call(message);
            //     ctx.writeAndFlush(resultBuf);
            // });

            // 2，用当前组的其他 EventLoop 来处理业务
            // ctx.executor().parent().next().execute(() -> {
            //     ByteBuf resultBuf = callBack.call(message);
            //     ctx.writeAndFlush(resultBuf);
            // });

            // 3，自己创建线程池执行业务
        }
    }
}