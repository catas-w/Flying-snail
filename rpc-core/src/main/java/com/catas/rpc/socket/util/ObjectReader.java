package com.catas.rpc.socket.util;

import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.enumeration.PackageType;
import com.catas.rpc.enumeration.RPCError;
import com.catas.rpc.exception.RPCException;
import com.catas.rpc.serializer.CommonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;


/**
 * 编码
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
@Slf4j
public class ObjectReader {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream in) throws IOException {
        byte[] numBytes = new byte[4];

        in.read(numBytes);
        int magicNumber = bytesToInt(numBytes);
        if (magicNumber != MAGIC_NUMBER) {
            log.error("不支持的协议类型");
            throw new RPCException(RPCError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(numBytes);
        int pkgCode = bytesToInt(numBytes);
        Class<?> packageClass;
        if (pkgCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RPCRequest.class;
        } else if (pkgCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RPCResponse.class;
        } else {
            log.error("无法识别数据包");
            throw new RPCException(RPCError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(numBytes);
        int serializerCode = bytesToInt(numBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            log.error("无法识别反序列化器");
            throw new RPCException(RPCError.UNKNOWN_SERIALIZER);
        }

        in.read(numBytes);
        int length = bytesToInt(numBytes);
        byte[] data = new byte[length];
        in.read(data);

        return serializer.deSerialize(data, packageClass);
    }

    /**
     * 字节数组转为 int
     */
    private static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF) << 24) |
                ((src[1] & 0xFF) << 16) |
                ((src[2] & 0xFF) << 8) |
                (src[3] & 0xFF);

        return value;
    }
}
