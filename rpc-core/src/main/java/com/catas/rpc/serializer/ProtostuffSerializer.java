package com.catas.rpc.serializer;

import com.catas.rpc.enumeration.SerializerCode;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer implements CommonSerializer{


    // 避免每次都申请空间
    private final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    // 缓存类对应的 schema
    private final Map<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<>();

    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object message = schema.newMessage();
        // 反序列化
        ProtobufIOUtil.mergeFrom(bytes, message, schema);
        return message;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOSTUFF.getCode();
    }

    private Schema getSchema(Class clazz) {
        Schema<?> schema = schemaMap.get(clazz);
        if (Objects.isNull(schema)) {
             schema = RuntimeSchema.getSchema(clazz);
             if (Objects.nonNull(schema)) {
                 schemaMap.put(clazz, schema);
             }
        }
        return schema;
    }
}
