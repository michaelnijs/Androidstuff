package be.michaelnijs.heliosmanager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Communicator {

    String serveruri = "";

    public Communicator(String serveruri) {
        this.serveruri = serveruri;
    }

    public void setServerUri(String serveruri) {
        this.serveruri = serveruri;
    }

    public String getServerUri() {
        return this.serveruri;
    }

    public Communicator() {
        // Nothing to do, just to create it
    }

    private boolean isReady() {
        if (this.serveruri != "") {
            return true;
        } else {
            return false;
        }
    }

    public void sendRGBValue(int r, int g, int b) throws IOException {
        if (isReady()) {
        String crafturl = this.serveruri + "/setRgb?r=" + r + "&g=" + g + "&b=" + b;
        String result = fetchURL(crafturl);
        }
    }

    public void toggleRGBLed() throws IOException {
        if (isReady()) {
        String crafturl = this.serveruri + "/toggleRGBLeg";
        String result = fetchURL(crafturl);
        }
    }

    public void sendText(String ledtext) throws IOException {
        if (isReady()) {
        String crafturl = this.serveruri + "/setLedText?text=" + ledtext;
        String result = fetchURL(crafturl);
        }
    }

    public int fetchTemperature() throws IOException {
        if (isReady()) {
        String crafturl = this.serveruri + "/getTemp";
        return Integer.parseInt(fetchURL(crafturl));
        } else {
            return -1;
        }
    }

    public int fetchLightSensor() throws IOException {
        if (isReady()) {
        String crafturl = this.serveruri + "/getLight";
        return Integer.parseInt(fetchURL(crafturl));
        } else {
            return -1;
        }
    }

    private String fetchURL(String url) {
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            StatusLine statusline = response.getStatusLine();

            if (statusline.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);

                out.close();
                result = out.toString();
                return result;

            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusline.getReasonPhrase());
            }

        } catch (IOException ex) {
            //throw new IOException("Communication failure");
            return result;
        }

    }


}
