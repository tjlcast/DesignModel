package com.tjlcast.SingleMode.others;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangjialiang on 2017/10/18.
 *
 * 设备相关数据，映射集合
 */
public class Device {

    private String uId ;
    private String deviceAccess ;
    private String deviceId ;
    private String deviceName ;
    private String info ;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDeviceAccess() {
        return deviceAccess;
    }

    public void setDeviceAccess(String deviceAccess) {
        this.deviceAccess = deviceAccess;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Device(){

    }

    public Device(String uId, String deviceName) {
        this.uId = uId ;
        this.deviceName = deviceName ;
    }

    public Map toMap() {
        HashMap<String, String> map = new HashMap<>() ;
        map.put("uId", getuId()) ;
        map.put("deviceAccess", getDeviceAccess()) ;
        map.put("deviceId", getDeviceId()) ;
        map.put("deviceName", getDeviceName()) ;
        map.put("info", getInfo()) ;
        return map ;
    }

    @Override
    public String toString() {
        return "Device{" +
                "uId='" + uId + '\'' +
                ", deviceAccess='" + deviceAccess + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}'+info;
    }

    public Device(String uId, String deviceAccess, String deviceId, String deviceName) {
        this.uId = uId ;
        this.deviceAccess = deviceAccess ;
        this.deviceId = deviceId ;
        this.deviceName = deviceName ;
    }

}
