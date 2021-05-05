package com.catas.rpc.serializer;


import com.catas.rpc.entity.RPCRequest;
import com.catas.rpc.enumeration.SerializerCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonSerializer implements CommonSerializer{


    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("序列化时出现异常, {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object deSerialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RPCRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("反序列化时出现异常, {}", e.getMessage());
            return null;
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }

    /**
     * @Description: 根据object 类型将其反序列化
     */
    public Object handleRequest(Object obj) throws IOException {
        RPCRequest request = (RPCRequest) obj;
        for (int i=0; i<request.getArgTypes().length; i++) {
            Class<?> clazz = request.getArgTypes()[i];
            if (!clazz.isAssignableFrom(request.getArguments()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getArguments()[i]);
                request.getArguments()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return request;
    }
}
