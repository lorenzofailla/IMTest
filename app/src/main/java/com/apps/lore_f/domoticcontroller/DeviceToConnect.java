package com.apps.lore_f.domoticcontroller;

/**
 * Created by lore_f on 26/08/2017.
 */

public class DeviceToConnect {

    private String deviceName;
    private String location;
    private boolean isUnix;
    private boolean hasDirectoryNavigation;
    private boolean hasTorrentManagement;
    private boolean hasVideoSurveillance;
    private boolean hasWakeOnLan;
    private String cameraNames;
    private String cameraIDs;

    // empty constructor
    public DeviceToConnect() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getHasTorrentManagement() {
        return hasTorrentManagement;
    }

    public boolean getHasDirectoryNavigation() {
        return hasDirectoryNavigation;
    }

    public boolean getHasWakeOnLan() {
        return hasWakeOnLan;
    }

    public boolean getHasVideoSurveillance() {
        return hasVideoSurveillance;
    }

    public String getCameraNames() { return cameraNames; }

    public String getCameraIDs() { return cameraIDs; }

}


