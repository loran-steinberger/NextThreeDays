package com.example.loran.nextthreedays;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/*
* LIST OF TODOS
* Parsing the website html: write getNextEvent() in Webpage
* Pretty up the xml
*
* Apply filters to stored events when user selects: direct filter functions to the "Day" class
* Pair event type selection with images from website: easy enough, maybe heavy on xml coding
*
* Optional
* Search function
* leverage fragments
* Structure of the website:
* c means city
* t means type
* Date means date
* numbers correspond to selection of multiple categories
*
*http://nextthreedays.com/mobile/MobileWebsite.cfm?Date=4/27/15&c=Radford&t=4,3,2,1
*
* */

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, GestureOverlayView.OnGesturePerformedListener, MultiSpinner.MultiSpinnerListener {


    private GestureLibrary gestureLib;

    private Spinner citySpinner;
    private MultiSpinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //backend for user filters
        //Gesture support setup
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        setContentView(gestureOverlayView);

        //setup for Views in Main Activity
        ImageView logo =(ImageView) findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.n3dlogo);
        citySpinner = (Spinner) findViewById(R.id.spinnerCity);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        citySpinner.setAdapter(adapter);

        typeSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
        
        ArrayAdapter<CharSequence> adapterE = ArrayAdapter.createFromResource(this, R.array.events_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //typeSpinner.setItems(new ArrayList<String>(), getString(R.array.events_array), this);
        typeSpinner.setAdapter(adapterE);


        citySpinner.setOnItemSelectedListener(this);
       // typeSpinner.setOnItemSelectedListener(this);

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

        //TODO add Favorites option
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String found = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, found, Toast.LENGTH_SHORT).show();

    }

    //TODO click function for Go!

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        for (Prediction prediction : predictions) {
            if (prediction.score > 1.0) {
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        //TODO
    }
}
