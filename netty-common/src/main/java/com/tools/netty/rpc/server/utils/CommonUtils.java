package com.tools.netty.rpc.server.utils;

import static com.tools.netty.rpc.common.Constant.SERVICE_CONCAT_TOKEN;

public class CommonUtils {

    public static String makeServiceKey(String interfaceName, String version) {
        String serviceKey = interfaceName;
        if (version != null && version.trim().length() > 0) {
            serviceKey += SERVICE_CONCAT_TOKEN.concat(version);
        }
        return serviceKey;
    }
}
