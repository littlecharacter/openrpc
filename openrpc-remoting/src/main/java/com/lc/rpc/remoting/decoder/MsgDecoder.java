package com.lc.rpc.remoting.decoder;

import com.lc.rpc.protocol.Message;
import com.lc.rpc.protocol.MsgHead;
import com.lc.rpc.serializer.ObjectSerializer;
import com.lc.rpc.serializer.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author gujixian
 * @since 2023/10/21
 */
public class MsgDecoder<T> extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= MsgHead.MSG_HEAD_LENGTH) {
            // 处理消息头
            byte[] headBytes = new byte[MsgHead.MSG_HEAD_LENGTH];
            // msgBuf.readBytes(headBytes);
            byteBuf.getBytes(byteBuf.readerIndex(), headBytes); // readIndex 不变
            MsgHead msgHead = new MsgHead();
            msgHead.setHead(headBytes);

            // 处理消息体
            if (byteBuf.readableBytes() >= msgHead.getDataLength()) {
                byteBuf.readBytes(headBytes); // 移动指针
                byte[] bodyBytes = new byte[msgHead.getDataLength()];
                byteBuf.readBytes(bodyBytes);
                ObjectSerializer serializer = new KryoSerializer();
                T msgBody = serializer.deserialize(bodyBytes);
                list.add(new Message<>(msgHead, msgBody));
            } else {
                break;
            }
        }
    }
}
