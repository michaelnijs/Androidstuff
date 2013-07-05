package be.michaelnijs.heliosmanager;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Michael on 7/5/13.
 */


public class WifiClientFinder {


    private final WifiManager globalWifiManager;
    private final int timeout = 300;

    public WifiClientFinder(Context context) {
        globalWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /*
    * Check if the AP is running, if not we can enable it
     */
    public void setWifiAP() throws Exception {
        try {

            globalWifiManager.setWifiEnabled(true);
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isWifiEnabled() {
        try {
            Method m = globalWifiManager.getClass().getMethod("getWifiApState");
            int tmp = ((Integer)method.invoke(globalWifiManager));

            if (WIFI_AP_STATE.class.getEnumConstants()[tmp] == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED) {
               return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }



    public ArrayList<WifiClient> getClientList() throws Exception {
        BufferedReader r = null;
        ArrayList<WifiClient> result = null;

        try {
            result = new ArrayList<WifiClient>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = br.readline()) != null) {
                // More lines to read
                String[] parts = line.split(" +");
                if ((parts != null) && (parts.length >= 4)) {
                    String mac = parts[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        // Fetch the ip and if you can reach it!
                        boolean isReach = InetAddress.getByName(parts[0]).isReachable(this.timeout);
                        if (isReach) {
                            result.add(new WifiClient(parts[0], parts[3], parts[5], isReach));
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw e;
        }


        return result;
    }

}
