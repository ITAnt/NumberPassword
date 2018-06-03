package com.itant.numberpassword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itant.npassword.NumberPassword;
import com.itant.npassword.OnPasswordChangeListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NumberPassword np_test = findViewById(R.id.np_test);
        np_test.setOnPasswordChangeListener(new OnPasswordChangeListener() {
            @Override
            public void onPasswordChange(String currentPasswordText, int maxPasswordLength) {
                Log.i("np", currentPasswordText);
                if (maxPasswordLength == currentPasswordText.length()) {
                    startActivity(new Intent(MainActivity.this, SecondActivity.class));
                }
            }
        });
    }
}
