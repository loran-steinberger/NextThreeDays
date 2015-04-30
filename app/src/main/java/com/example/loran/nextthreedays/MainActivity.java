package com.example.loran.nextthreedays;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, GestureOverlayView.OnGesturePerformedListener {


    private GestureLibrary gestureLib;

    private Spinner citySpinner;
    private Spinner typeSpinner;
    private ListView listView;
    private ArrayList<Event> eventList;

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

        typeSpinner = (Spinner) findViewById(R.id.spinnerEvent);
        
        ArrayAdapter<CharSequence> adapterE = ArrayAdapter.createFromResource(this,
                R.array.events_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //typeSpinner.setItems(new ArrayList<String>(), getString(R.array.events_array), this);
        typeSpinner.setAdapter(adapterE);


        citySpinner.setOnItemSelectedListener(this);
       // typeSpinner.setOnItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listView);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        String[] dateArray = sdf.format(date).split("/");

        URL url = null;
        try {
            url = new URL("http://nextthreedays.com/mobile/AjaxGetDayEvents.cfm?Date="
                    + dateArray[0].replaceFirst("^0+(?!$)", "") + "/" + dateArray[1].replaceFirst("^0+(?!$)", "") + "/" + dateArray[2].replaceFirst("^0+(?!$)", "") + "&c=&t=");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            eventList = new DownloadFilesTask().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<String> events = new ArrayList<String>();

        for (int i = 0; i < eventList.size(); i++) {
            events.add(eventList.get(i).toString(false));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                events );

        listView.setAdapter(arrayAdapter);

    }

    private class DownloadFilesTask extends AsyncTask<URL, Void, ArrayList<Event>> {
        @Override
        protected ArrayList<Event> doInBackground(URL... urls) {
            ArrayList<Event> list = new ArrayList<Event>();
            try {
                list = Tests.insertDate(urls[0].toExternalForm());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(list);
            return list;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Event> list) {
            super.onPostExecute(list);
            //setEventList(list);
        }
    }

    public void setEventList(ArrayList<Event> list) {
        for (int i = 0; i < list.size(); i++) {
            eventList.add(list.get(i));
        }
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

    //@Override
    public void onItemsSelected(boolean[] selected) {
        //TODO
    }
}
