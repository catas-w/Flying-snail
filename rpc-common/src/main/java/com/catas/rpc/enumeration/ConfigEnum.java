package com.catas.rpc.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigEnum {

    RPC_CONFIG_PATH("snail.properties"),
    ZK_ADDRESS("snail.zookeeper.address"),
    NACOS_ADDRESS("snail.nacos.address");


    private final String property;
}
