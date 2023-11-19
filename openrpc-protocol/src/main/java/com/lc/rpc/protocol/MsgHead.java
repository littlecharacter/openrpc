package com.lc.rpc.protocol;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author gujixian
 * @since 2023/10/19
 */
public class MsgHead implements Serializable {
    private static final int REQUEST_ID_OFFSET = 4;
    private static final int DATA_LENGTH_OFFSET = 12;
    // private byte magicHigh;
    // private byte magicLow;
    // /**
    //  * 总共8位：
    //  * 1）高 1 为 0 表示请求，为 1 表示响应
    //  * 2）高 。。。
    //  */
    // private byte flag;
    // private byte x;
    // private long requestId;
    // private int dataLength;
    private final byte[] head = new byte[16];

    public byte[] getHead() {
        return this.head;
    }

    public void setHead(byte[] head) {
        System.arraycopy(head, 0, this.head, 0, this.head.length);
    }

    public byte getMagicHigh() {
        return this.head[0];
    }

    public void setMagicHigh(byte magicHigh) {
        this.head[0] = magicHigh;
    }

    public byte getMagicLow() {
        return this.head[1];
    }

    public void setMagicLow(byte magicLow) {
        this.head[1] = magicLow;
    }

    public byte getFlag() {
        return this.head[2];
    }

    public void setFlag(byte flag) {
        this.head[2] = flag;
    }

    public byte getX() {
        return this.head[3];
    }

    public void setX(byte x) {
        this.head[3] = x;
    }

    public long getRequestId() {
        byte[] bytes = new byte[]{
                this.head[REQUEST_ID_OFFSET],
                this.head[REQUEST_ID_OFFSET + 1],
                this.head[REQUEST_ID_OFFSET + 2],
                this.head[REQUEST_ID_OFFSET + 3],
                this.head[REQUEST_ID_OFFSET + 4],
                this.head[REQUEST_ID_OFFSET + 5],
                this.head[REQUEST_ID_OFFSET + 6],
                this.head[REQUEST_ID_OFFSET + 7]
        };
        return ByteBuffer.wrap(bytes).getLong();
    }

    public void setRequestId(long requestId) {
        int index = 0;
        for (byte dl : ByteBuffer.allocate(8).putLong(requestId).array()) {
            this.head[REQUEST_ID_OFFSET + index++] = dl;
        }
    }

    public int getDataLength() {
        int offset = 12;
        byte[] bytes = new byte[]{
                this.head[DATA_LENGTH_OFFSET],
                this.head[DATA_LENGTH_OFFSET + 1],
                this.head[DATA_LENGTH_OFFSET + 2],
                this.head[DATA_LENGTH_OFFSET + 3]
        };
        return ByteBuffer.wrap(bytes).getInt();
    }

    public void setDataLength(int dataLength) {
        int index = 0;
        for (byte dl : ByteBuffer.allocate(4).putInt(dataLength).array()) {
            this.head[DATA_LENGTH_OFFSET + index++] = dl;
        }
    }

    public static void main(String[] args) {
        MsgHead msgHead = new MsgHead();
        msgHead.setMagicHigh((byte) 1);
        msgHead.setMagicLow((byte) 2);
        msgHead.setFlag((byte) -1);
        msgHead.setRequestId(2234111567890L);
        msgHead.setDataLength(12222222);

        System.out.println(msgHead.getRequestId());
        System.out.println(msgHead.getDataLength());
        System.out.println(msgHead.getHead().length);
    }
}
