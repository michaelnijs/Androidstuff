package be.michaelnijs.heliosmanager;

import android.app.Activity;
import android.widget.SeekBar;

/**
 * Created by Michael on 7/1/13.
 */
public class SeekBarActivityHandler implements SeekBar.OnSeekBarChangeListener {

    MainActivity parent;

    public SeekBarActivityHandler(MainActivity parent) {
        this.parent = parent;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        parent.updateRGB();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
