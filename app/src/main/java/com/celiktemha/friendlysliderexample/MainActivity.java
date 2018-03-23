package com.celiktemha.friendlysliderexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.celiktemha.friendlyslider.FriendlySliderView;
import com.celiktemha.friendlyslider.OnSliderValueChangeListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FriendlySliderView friendlySliderView = findViewById(R.id.friendlySliderView);
        friendlySliderView.setOnSliderValueChangeListener(new OnSliderValueChangeListener() {
            @Override
            public void onSliderValueChanged(int value) {
                Toast.makeText(MainActivity.this, "" + value, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
