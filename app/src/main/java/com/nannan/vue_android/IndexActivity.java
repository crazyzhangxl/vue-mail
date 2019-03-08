package com.nannan.vue_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nannan.vue_android.h5.H5Activity;

/**
 * @author crazyZhangxl on 2019-3-6 13:39:17.
 * Describe:
 */

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        findViewById(R.id.btnTurnH5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, H5Activity.class));
            }
        });


        findViewById(R.id.btnTurnVue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this,MainActivity.class));
            }
        });
    }
}
