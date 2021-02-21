package com.tools.netty.rpc.server.serializer;

public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
