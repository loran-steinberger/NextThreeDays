package com.example.loran.nextthreedays;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
* LIST OF TODOS
* Left and right swiping changes days: ontouch events in MainActivity
* Parsing the website html: write getNextEvent() in Webpage
* Storing all the events for a given day internally: perhaps another class representing a "Day"
* writing the "Event" Class
* Pretty up the xml
* Apply filters to stored events when user selects: direct filter functions to the "Day" class
* Pair event type selection with images from website: easy enough, maybe heavy on xml coding
*
* Optional
* Search function
* leverage fragments
*
*
* */

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    //TESTING GITHUBBB
    private final static String mobileSite = "http://www.nextthreedays.com/mobile/mobilewebsite.cfm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logo =(ImageView) findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.n3dlogo);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCity);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinnerE = (Spinner) findViewById(R.id.spinnerEvent);
        ArrayAdapter<CharSequence> adapterE = ArrayAdapter.createFromResource(this, R.array.events_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerE.setAdapter(adapterE);


        spinner.setOnItemSelectedListener(this);
        spinnerE.setOnItemSelectedListener(this);

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

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
