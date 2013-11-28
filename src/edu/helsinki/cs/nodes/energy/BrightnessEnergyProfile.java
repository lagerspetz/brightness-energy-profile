package edu.helsinki.cs.nodes.energy;

import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class BrightnessEnergyProfile extends Activity {

    /**
     * Default
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brightness_energy_profile);
        SeekBar bright = (SeekBar) findViewById(R.id.brightnessSeek);
        bright.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                // Get text field reference
                SeekBar bright = (SeekBar) findViewById(R.id.brightnessSeek);

                // Brightness, 0 to 255
                int brightnessLevel = bright.getProgress();

                // Save brightness in phone settings
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        brightnessLevel);
                // Apply brightness to our app
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                float brightness = 1.0f / 255 * brightnessLevel;
                if (brightness == 0)
                    brightness = 0.01f;
                lp.screenBrightness = brightness;
                getWindow().setAttributes(lp);
                updateEstimate(brightnessLevel);
                TextView brn = (TextView) findViewById(R.id.brightnessValue);
                brn.setText(brightnessLevel+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Unused
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Unused
            }
        });
    }

    /**
     * Default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_brightness_energy_profile,
                menu);
        return true;
    }

    /**
     * Show the current brightness in the text field
     */
    public void showBrightness() {
        SeekBar bright = (SeekBar) findViewById(R.id.brightnessSeek);
        int b = getScreenBrightness();
        bright.setProgress(b);
        TextView brn = (TextView) findViewById(R.id.brightnessValue);
        brn.setText(b+"");
    }

    /**
     * Get current screen brightness.
     */
    public int getScreenBrightness() {
        int screenBrightnessValue = 0;
        try {
            screenBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return screenBrightnessValue;
    }

    /**
     * Update estimated energy use based on brightness.
     * 
     * @param brightness
     *            a number from -1 to 255.
     */
    public void updateEstimate(int brightness) {
        // TODO: Use actual model here
        double s4_max = 933.48;
        double s4_150 = 766.77;
        double s4_min = 632.21;

        double estimate = 0.0;
        // Take exact values
        switch (brightness) {
        case 255:
            estimate = s4_max;
            break;
        case 150:
            estimate = s4_150;
            break;
        case 0:
            estimate = s4_min;
            break;
        default:
            break;
        }
        // Rough estimate for non-exact values
        if (estimate == 0) {
            if (brightness < 255 && brightness > 150) {
                double multiplier = (brightness - 150.0) / (255 - 150.0);
                estimate = multiplier * (s4_max - s4_150) + s4_150;
            } else if (brightness < 150 && brightness > 0) {
                double multiplier = brightness / 150.0;
                estimate = multiplier * (s4_150 - s4_min) + s4_min;
            }
        }
        // Update field on screen
        if (estimate != 0) {
            TextView est = (TextView) findViewById(R.id.estimate);
            est.setText(estimate + " mW");
        }
    }

    /*
     * Checks for brightness change on resume and updates the text field.
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        showBrightness();
    }
}