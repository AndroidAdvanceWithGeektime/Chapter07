
package com.sample.systrace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "[onResume]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.test_gc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TestIgnoreFile.TestGc();
            }
        });
    }

}
