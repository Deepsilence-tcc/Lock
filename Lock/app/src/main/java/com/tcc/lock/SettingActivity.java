package com.tcc.lock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {


    private List<Integer> passList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final GestureLock lock = (GestureLock) findViewById(R.id.lock_set);
        Button btnSave = (Button) findViewById(R.id.btn_save);
        Button btnReset = (Button) findViewById(R.id.btn_reset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lock.resetPoint();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingActivity.this.passList!=null) {
                    StringBuilder sb = new StringBuilder();
                    for (Integer i : passList) {
                        sb.append(i);
                    }
                    SharedPreferences sp = SettingActivity.this.getSharedPreferences("password",SettingActivity.this.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("pass",sb.toString());
                    editor.commit();
                }
            }
        });
        lock.setOnDrawFinishedListener(new GestureLock.onDrawFinishListener() {
            @Override
            public boolean onDrawFinish(List<Integer> passList) {
                if (passList.size() < 3) {
                    Toast.makeText(SettingActivity.this, "密码长度大于3", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    SettingActivity.this.passList = passList;
                    return true;
                }
            }
        });
    }


}
