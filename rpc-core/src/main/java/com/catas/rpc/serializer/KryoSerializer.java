package com.catas.rpc.serializer;


import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.entity.RPCResponse;
import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.exception.SerializeException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

@Slf4j
public class KryoSerializer implements CommonSerializer{

    // kryo中io非线程安全, 使用threadlocal 初始化
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(new Supplier<Kryo>() {
        @Override
        public Kryo get() {
            Kryo kryo = new Kryo();
            kryo.register(RPCRequest.class);
            kryo.register(RPCResponse.class);
            // 循环引用检测
            kryo.setReferences(true);
            // 不要求强制注册类
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Output output = new Output(outputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("序列化时出现异常");
            throw new SerializeException("序列化时出现异常");
        }
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        try  (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
              Input input = new Input(inputStream);){
            Kryo kryo = kryoThreadLocal.get();
            Object readObject = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return readObject;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("反序列化时出现异常");
            throw new SerializeException("反序列化时出现异常");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
