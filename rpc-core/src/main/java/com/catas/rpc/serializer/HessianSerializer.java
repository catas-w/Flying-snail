package com.catas.rpc.serializer;


import com.catas.rpc.enumeration.SerializerCode;
import com.catas.rpc.exception.SerializeException;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianSerializer implements CommonSerializer{

    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(outputStream);
            hessianOutput.writeObject(obj);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("序列化时出现异常, {}", e.getMessage());
            throw new SerializeException("序列化时出现异常");
        }finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    log.error("关闭output流时出现异常");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(inputStream);
            return hessianInput.readObject();
        } catch (Exception e) {
            log.error("反序列化时出现异常, {}", e.getMessage());
            throw new SerializeException("反序列化时出现异常");
        } finally {
            if (hessianInput != null) {
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.HESSIAN.getCode();
    }
}
