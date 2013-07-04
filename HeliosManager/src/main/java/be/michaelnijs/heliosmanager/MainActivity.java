package be.michaelnijs.heliosmanager;


import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.content.*;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    SeekBarActivityHandler sbah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendText = (Button) findViewById(R.id.button);
        sendTextedit = (EditText) findViewById(R.id.editText);
        context = this;

        temperature = (TextView) findViewById(R.id.textView3);
        illumination = (TextView) findViewById(R.id.textView7);


        seekred = (SeekBar) findViewById(R.id.seekBar);
        seekgreen = (SeekBar) findViewById(R.id.seekBar2);
        seekblue = (SeekBar) findViewById(R.id.seekBar3);
        ledtoggler = (CheckBox) findViewById(R.id.checkBox);

        talker = new Communicator();
        sbah = new SeekBarActivityHandler(this);

        seekred.setOnSeekBarChangeListener(sbah);
        seekgreen.setOnSeekBarChangeListener(sbah);
        seekblue.setOnSeekBarChangeListener(sbah);

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
    
}
