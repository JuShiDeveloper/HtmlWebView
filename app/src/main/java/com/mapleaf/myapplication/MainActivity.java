package com.mapleaf.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapleaf.myapplication.moveEdittext.EditTextActivity;
import com.mapleaf.myapplication.webview.WebViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jump_move_editText).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, EditTextActivity.class));
        });

        findViewById(R.id.jump_webView).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WebViewActivity.class));
        });
    }
}
