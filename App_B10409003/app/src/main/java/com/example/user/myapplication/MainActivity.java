package com.example.user.myapplication;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int go = item.getItemId();
        if (go == R.id.next_page) {

            //toActivity2
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Main2Activity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}