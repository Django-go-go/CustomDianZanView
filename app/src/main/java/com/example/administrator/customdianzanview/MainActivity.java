package com.example.administrator.customdianzanview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DianZanView mDianZanView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDianZanView = (DianZanView) findViewById(R.id.dianzan);
        mDianZanView.initNumber(989);
        mDianZanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDianZanView.start();
            }
        });

    }
}
