package com.lc.rpc.protocol;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class Message<T> {
    private MsgHead msgHead;
    private T msgBody;

    public Message(MsgHead msgHead, T msgBody) {
        this.msgHead = msgHead;
        this.msgBody = msgBody;
    }

    public MsgHead getMsgHead() {
        return msgHead;
    }

    public void setMsgHead(MsgHead msgHead) {
        this.msgHead = msgHead;
    }

    public T getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(T msgBody) {
        this.msgBody = msgBody;
    }
}
