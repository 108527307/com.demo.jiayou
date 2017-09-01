package com.example.jiayou;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/9 0009.
 */
public class AboutUs_activity extends ActivityManager {

        private Button back;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);
            back= (Button) findViewById(R.id.iv_back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

}
