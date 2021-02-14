package com.alishangtian.macos.remoting.netty;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * @Description CompactObjectOutputStream
 * @Date 2020/6/4 上午9:20
 * @Author maoxiaobing
 **/
public class CompactObjectOutputStream extends ObjectOutputStream {
    static final int TYPE_FAT_DESCRIPTOR = 0;
    static final int TYPE_THIN_DESCRIPTOR = 1;

    CompactObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        writeByte(STREAM_VERSION);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        Class<?> clazz = desc.forClass();
        if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface() ||
                desc.getSerialVersionUID() == 0) {
            write(TYPE_FAT_DESCRIPTOR);
            super.writeClassDescriptor(desc);
        } else {
            write(TYPE_THIN_DESCRIPTOR);
            writeUTF(desc.getName());
        }
    }
}