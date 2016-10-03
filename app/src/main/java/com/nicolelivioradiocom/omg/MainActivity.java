package com.nicolelivioradiocom.omg;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.*;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String hitType[] = {"Missed", "Hit", "Clipped wing", "Still alive", "Scared", "Nope"};
    TextView mHunterText;
    Button mGenerateHitButton;
    ImageView mTurkeyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHunterText = (TextView) findViewById(R.id.descriptionText);
        mTurkeyImage = (ImageView) findViewById(R.id.turkeyImage);
        mGenerateHitButton = (Button) findViewById(R.id.huntButton);

        mGenerateHitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                int index = new Random().nextInt(hitType.length);
                mHunterText.setText(hitType[index]);

                // YoYo.with(Techniques.Swing).duration(500).playOn(mTurkeyImage);
                YoYo.with(Techniques.Pulse).duration(500).playOn(mTurkeyImage);

                // Segue to next Activity
                Intent i = new Intent(getBaseContext(), GameActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
