package com.example.asy.mobileproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Statistics extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent(); //전달된 인텐트
        String textIn = intent.getStringExtra("TextIn");

        textView.setText(textIn);

    }
}
