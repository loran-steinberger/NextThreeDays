package com.example.loran.nextthreedays;

import android.annotation.TargetApi;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.AsyncTask;

import android.os.Build;
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
import android.widget.TextView;
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

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, GestureOverlayView.OnGesturePerformedListener, ListView.OnItemClickListener{


    private GestureLibrary gestureLib;

    private Spinner citySpinner;
    private Spinner typeSpinner;
    private TextView dateView;
    private ListView listView;
    private ArrayList<Event> eventList;
    private ArrayList<Event> favs;
    private Bundle saved;
    private String cityName;
    private String typeNumber;
    private URL url;
    private String day;
    private String month;
    private String year;
    private Pebble pebble;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        pebble = new Pebble(this);

        this.saved = savedInstanceState;
        this.cityName = "";
        this.typeNumber = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(favs == null) favs = new ArrayList<>();
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
        typeSpinner.setOnItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listView);
        dateView = (TextView) findViewById(R.id.textDate);

        listView.setOnItemClickListener(this);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        String[] dateArray = sdf.format(date).split("/");
        day = dateArray[1].replaceFirst("^0+(?!$)", "");
        month = dateArray[0].replaceFirst("^0+(?!$)", "");
        year = dateArray[2].replaceFirst("^0+(?!$)", "");


        dateView.setText(month + "/" + day + "/" + year);

//        URL url = null;

        regenerateList();


    }

    private void regenerateList() {
        try {
            url = new URL("http://nextthreedays.com/mobile/AjaxGetDayEvents.cfm?Date="
                    + month  + "/" + day + "/" + year + "&c=" + cityName + "&t=" + typeNumber);
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


        List<String> events = new ArrayList<>();

        for (int i = 0; i < eventList.size(); i++) {
            events.add(eventList.get(i).toString(false));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                events );

        listView.setAdapter(arrayAdapter);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event found = (Event) eventList.get(position);
        if(favs.contains(found)) {
            favs.remove(found);
            Toast.makeText(this, "unfavorited!", Toast.LENGTH_SHORT).show();
            pebble.sendAlertToPebble("You have set" + found.toString(true) + "as a favorite!");
        }
        else {
            favs.add(found);
            Toast.makeText(this, "favorited!", Toast.LENGTH_SHORT).show();
            pebble.sendAlertToPebble("You have set" + found.toString(true) + "as a favorite!");
        }
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

    private void increaseDay() {
        int dae = Integer.parseInt(this.day);
        if(dae < 28) {
            day = Integer.toString(++dae);
            return;
        }
        int mon = Integer.parseInt(this.month);
        if(mon == 2) {
            month = "3";
            day = "1";
            return;
        }
        if(dae < 30) {
            day = Integer.toString(++dae);
            return;
        }
        if(mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            month = Integer.toString(++mon);
            day = "1";
            return;
        }
        if(dae < 31) {
            day = Integer.toString(++dae);
            return;
        }
        if(mon == 12) {
            int yer = Integer.parseInt(year);
            year = Integer.toString(++yer);
            month = "1";
            day = "1";
            return;
        }
        month = Integer.toString(++mon);
        day = "1";
    }

    public void decreaseDay() {
        int dae = Integer.parseInt(this.day);
        if(dae > 1) {
            day = Integer.toString(--dae);
            return;
        }
        int mon = Integer.parseInt(this.month);
        if(mon > 1) {
            month = Integer.toString(--mon);
        }
        else {
            int yer = Integer.parseInt(year);
            year = Integer.toString(--yer);
            month = "12";
        }

        if(mon == 2) day = "28";
        else if(mon == 4 || mon == 6 || mon == 9 || mon == 11) day = "30";
        else day = "31";
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
        int pos = item.getOrder();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.favorites) {

            List<String> events = new ArrayList<>();

            for (int i = 0; i < favs.size(); i++) {
                events.add(favs.get(i).toString(false));
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    events);

            listView.setAdapter(arrayAdapter);
        }
        if(id == R.id.search) {
            List<String> events = new ArrayList<>();

            for (int i = 0; i < eventList.size(); i++) {
                events.add(eventList.get(i).toString(false));
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    events);

            listView.setAdapter(arrayAdapter);
        }

        //TODO add Favorites option
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        String found = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, found, Toast.LENGTH_SHORT).show();

        if (found.equals("All Cities")) {
            cityName = "";
        }
        else if(found.equals("All Events")){
            typeNumber = "";
        }
           else if(found.equals("Blacksburg") || found.equals("Christiansburg")||found.equals("Radford")) {
            cityName = found;
        } else{
                if(found.equals("Music")){
                    typeNumber = "1";
                } else if(found.equals("Food")){
                    typeNumber = "2";
                } else if(found.equals("Drinks")){
                    typeNumber = "3";
                } else if(found.equals("Sports")){
                    typeNumber = "4";
                } else if(found.equals("Misc")){
                    typeNumber = "5";
                } else if(found.equals("Family")){
                    typeNumber = "6";
                } else if(found.equals("Arts")){
                    typeNumber = "7";
                } else if(found.equals("Speaking")){
                    typeNumber = "8";
                } else if(found.equals("Business")){
                    typeNumber = "9";
                } else if(found.equals("Charity")){
                    typeNumber = "10";
                }
            }

            regenerateList();

    }



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
                if(prediction.name.equals("next")) {
                    increaseDay();
                    regenerateList();
                }
                else if(prediction.name.equals("previous")) {
                    decreaseDay();
                    regenerateList();
                }
                dateView.setText(month + "/" + day + "/" + year);
            }
        }
    }

}
