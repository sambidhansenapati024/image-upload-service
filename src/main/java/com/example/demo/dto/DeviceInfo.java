package com.example.demo.dto;


public class DeviceInfo {

    private String browser;
    private String operatingSystem;
    private String device;
    private String ipAddress;

    public DeviceInfo() {
    }

    public DeviceInfo(String browser,
                      String operatingSystem,
                      String device,
                      String ipAddress) {
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.device = device;
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
