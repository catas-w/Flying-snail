package com.catas.rpc.serializer;

/**
 * @Description:  通用序列化接口
 */
public interface CommonSerializer {

    byte[] serialize(Object obj);

    Object deSerialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtostuffSerializer();
            default:
                return null;
        }
    }
}
