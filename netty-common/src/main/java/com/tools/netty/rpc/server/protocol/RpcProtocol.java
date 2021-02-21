package com.tools.netty.rpc.server.protocol;

import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 7924792256335017223L;

    private String host;

    private int port;

    private List<RpcServiceInfo> serviceInfoList;

    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(null==o||this.getClass()!=o.getClass()){
            return false;
        }
        RpcProtocol that=(RpcProtocol)o;
        return Objects.equals(that.host,this.host)&&
                Objects.equals(that.port,this.port)&&
                isListEquals(that.serviceInfoList,this.serviceInfoList);
    }

    private boolean isListEquals(List<RpcServiceInfo> thatList,List<RpcServiceInfo> thisList){
        if (thisList == null && thatList == null) {
            return true;
        }
        if ((thisList == null && thatList != null)
                || (thisList != null && thatList == null)
                || (thisList.size() != thatList.size())) {
            return false;
        }
        return thisList.containsAll(thatList) && thatList.containsAll(thisList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serviceInfoList.hashCode());
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}
