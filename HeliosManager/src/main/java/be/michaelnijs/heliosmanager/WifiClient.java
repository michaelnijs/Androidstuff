package be.michaelnijs.heliosmanager;

/**
 * Created by micmast on 7/5/13.
 */
public class WifiClient {

    private String ipaddress;
    private String macaddress;
    private String device;
    private boolean isReachable;

    public WifiClient(String ip, String mac, String dev, boolean reachable){
        this.ipaddress = ip;
        this.macaddress = mac;
        this.device = dev;
        this.isReachable = reachable;
    }

    public String getIP(){
        return this.ipaddress;
    }
    public void setIP(String ip) {
        this.ipaddress = ip;
    }
    public String getMac() {
        return this.macaddress;
    }
    public void SetMac(String mac) {
        this.macaddress = mac;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getDevice() {
        return this.device;
    }

    public void setReachable(boolean reachable) {
        this.isReachable = reachable;
    }
    public boolean isReachable() {
        return this.isReachable;
    }
}
