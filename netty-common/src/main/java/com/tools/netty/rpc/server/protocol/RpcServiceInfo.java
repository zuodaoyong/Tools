package com.tools.netty.rpc.server.protocol;

import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class RpcServiceInfo implements Serializable {
    private static final long serialVersionUID = 2333289595033544662L;

    private String serviceName;

    private String version;


    @Override
    public boolean equals(Object o) {
        if(this==o){
            return true;
        }
        if(o==null||getClass()!=o.getClass()){
            return false;
        }
        RpcServiceInfo that=(RpcServiceInfo)o;
        return Objects.equals(that.serviceName,this.serviceName)&&
                Objects.equals(that.version,this.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, version);
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}
