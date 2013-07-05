package be.michaelnijs.heliosmanager;


import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.content.*;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {


    Button sendText;
    EditText sendTextedit;
    TextView temperature;
    TextView illumination;
    SeekBar seekred;
    SeekBar seekgreen;
    SeekBar seekblue;
    Context context;

    CheckBox ledtoggler;

    Communicator talker;
    WifiClientFinder wififinder;

    SeekBarActivityHandler sbah;

    ListView devices;
    ArrayList<WifiClient> wificlients = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendText = (Button) findViewById(R.id.button);
        sendTextedit = (EditText) findViewById(R.id.editText);
        context = this;

        temperature = (TextView) findViewById(R.id.textView3);
        illumination = (TextView) findViewById(R.id.textView7);

        devices = (ListView) findViewById(R.id.listView);

        seekred = (SeekBar) findViewById(R.id.seekBar);
        seekgreen = (SeekBar) findViewById(R.id.seekBar2);
        seekblue = (SeekBar) findViewById(R.id.seekBar3);
        ledtoggler = (CheckBox) findViewById(R.id.checkBox);

        talker = new Communicator();
        sbah = new SeekBarActivityHandler(this);

        seekred.setOnSeekBarChangeListener(sbah);
        seekgreen.setOnSeekBarChangeListener(sbah);
        seekblue.setOnSeekBarChangeListener(sbah);

        wififinder = new WifiClientFinder(context);

        ArrayList<String> listcontent = new ArrayList<String>();
        try {
            wificlients = wififinder.getClientList();
            for (int i = 0; i< wificlients.size(); i++) {
                listcontent.add(wificlients.get(i).getDevice() + " (" + wificlients.get(i).getIP() + ")");
            }



        } catch (Exception e) {
            alertMsg("Wifi AP error", "I could not create a list of clients.");
            listcontent.add("Failed to fetch");
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, listcontent);
        devices.setAdapter(adapter);

        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                talker.setServerUri(item.split("(")[1].split(")")[0]);


            }


        });

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    talker.sendText(sendTextedit.getText().toString());
                } catch (Exception ex) {
                    alertMsg("Communication Error", "Failed to set the led text.");
                }
            }
        });

        ledtoggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    talker.toggleRGBLed();
                } catch (Exception e) {
                    alertMsg("Communication Error", "Failed to toggle the led");
                }
            }
        });

    }

    public void updateRGB() {
        fetchRGB(seekred.getProgress(), seekgreen.getProgress(), seekblue.getProgress());
    }

    public void fetchRGB(int red, int green, int blue) {
        try {
            talker.sendRGBValue(red, green, blue);
        } catch (Exception ex) {
            alertMsg("Communication Error", "Failed to set the RGB values. Are you properly connected?");
        }
    }

    public void getIllumination() {
        try {
            illumination.setText(talker.fetchLightSensor() + " lux");
        } catch (Exception ex) {
            alertMsg("Communication Error", "Failed to get light sensor value");
        }
    }

    public void getTemperature() {
        try {
            temperature.setText(talker.fetchTemperature() + " °C");
        } catch (Exception ex) {
            alertMsg("Communication Error", "Failed to get temperature value");
        }
    }

    public void alertMsg(String title, String message) {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(context);
        alertdialogbuilder.setTitle(title);
        alertdialogbuilder.setMessage(message);
        AlertDialog alertDialog = alertdialogbuilder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
