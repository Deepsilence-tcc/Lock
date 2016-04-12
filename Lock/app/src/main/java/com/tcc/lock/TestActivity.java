package com.tcc.lock;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GestureLock testLock = (GestureLock) findViewById(R.id.lock_test);
        SharedPreferences sp =getSharedPreferences("password",MODE_PRIVATE);

        final String pass = sp.getString("pass",null);

        testLock.setOnDrawFinishedListener(new GestureLock.onDrawFinishListener() {
            @Override
            public boolean onDrawFinish(List<Integer> passList) {

                StringBuilder sb = new StringBuilder();
                for (Integer  i: passList) {
                    sb.append(i);
                }
                if (pass.equals(sb.toString())){
                    Toast.makeText(TestActivity.this,"正确",Toast.LENGTH_SHORT).show();
                    return true;

                }else {
                    Toast.makeText(TestActivity.this,"错误",Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });
    }

}
