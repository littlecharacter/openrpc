import com.lc.rpc.protocol.RequestBody;
import com.lc.rpc.remoting.decoder.MsgDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;

/**
 * @author gujixian
 * @since 2023/11/22
 */
public class TEST {
    public static void main(String[] args) throws IOException {
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
                        }
                    }).bind(10929);
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
        System.in.read(new byte[1024]);
    }
}
