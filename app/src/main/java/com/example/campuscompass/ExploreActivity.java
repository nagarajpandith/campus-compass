package com.example.campuscompass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    Button explore;
    TextView wifi_name;
    Spinner source, dest;
    WifiManager wifiManager;
    int curFloor;
    int selectedLevel;
    String []places = {"BTL10", "BTL07", "BT HOD Cabin", "LH210", "LH211", "LH212", "Dept of Physical Education", "BT Staffroom", "Biokinetics Lab", "Instrumentation and Project Lab", "LH306", "LH308", "LH309", "LH309", "LH310", "LH311", "LH312", "EC Staffroom", "CS Staffroom", "Texas Instruments Lab", "CSL01", "CSL02", "CSL03", "CSL04", "CSL05", "CSL06", "CSL07", "CSL05", "CS HOD Cabin", "CS Staffrom 4th Floor", "ISL01", "ISL02", "ISL03", "Project and Research Lab PG", "LH500", "LH501", "LH502", "LH503", "LH504", "LH505", "LH506", "CSE Library", "CSL08", "CFR03", "CS Staffrom 5th Floor"};
    int []placesLevels={2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5};
    List <String> wifi=new ArrayList<>(Arrays.asList("AndroidAP","Redmi7"));
    List<String> wifi2 = new ArrayList<>(Arrays.asList("BT-STUDENT","BT-STAFF"));
    List<String> wifi3 = new ArrayList<>(Arrays.asList("LH310-WiFi","LH312-WiFi","CCPLAB-WiFi"));
    List<String> wifi4 = new ArrayList<>(Arrays.asList("CS STUDENT","ECSTAFF-WiFi"));
    List<String> wifi5 = new ArrayList<>(Arrays.asList("ADALAB","BT-STAFF","CCPLAB-WiFi"));

    Location src=null,desti=null;

    Deque<Location> traverse=new LinkedList<Location>();
    Deque<Location> smallest=new LinkedList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        makeLocations();
        explore=findViewById(R.id.showPath);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),PathActivity.class);
                startActivity(i);
            }
        });
        Button[] pills = new Button[4];
        source = findViewById(R.id.source);
        dest = findViewById(R.id.dest);

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places);
        source.setAdapter(sourceAdapter);
        ArrayAdapter<String> destAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places);
        dest.setAdapter(destAdapter);

        wifi_name=findViewById(R.id.wifi_name);
        checkLocation();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    Toast.makeText(getApplicationContext(), "WiFi scan failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else{
            wifiManager.startScan();

        }

        source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                src=getLocation(places[i],placesLevels[i]);
                if(src !=null && desti!=null){
                    resetInRoute();
                    setRoute(src,desti);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                desti=getLocation(places[i],placesLevels[i]);
                if(src !=null && desti!=null){
                    resetInRoute();
                    setRoute(src,desti);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame,new ThirdFloor());
        ft.commit();

        for (int i = 0; i < pills.length; i++) {
            pills[i] = findViewById(getResources().getIdentifier("pill" + (i + 1), "id", getPackageName()));
            final int index = i;
            pills[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < pills.length; j++) {
                        if (j == index) {
                            selectedLevel = index+2;
                            pills[j].setBackgroundResource(R.drawable.selected_pill);
                            if(index==0){replaceFragment(new SecondFloor());}
                            if(index==1){replaceFragment(new ThirdFloor());}
                            if(index==2){replaceFragment(new FourthFloor());}
                            if(index==3){replaceFragment(new FifthFloor());}
                        } else {
                            pills[j].setBackgroundResource(R.drawable.pill_tab);
                        }
                    }
                }
            });
        }

        // ToDo: set selected_pill floor button by nearest WiFi name
    }

//    private List<String> getFilteredPlaces() {
//        List<String> filteredPlaces = new ArrayList<>();
//        for (int i = 0; i < places.length; i++) {
//            if (placesLevels[i] == selectedLevel) {
//                filteredPlaces.add(places[i]);
//            }
//        }
//        return filteredPlaces;
//    }

    private void replaceFragment(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame,f);
        ft.commit();
    }

    private void scanSuccess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        List<ScanResult> results = wifiManager.getScanResults();
        if(!results.isEmpty()){
            wifi_name.setText(results.get(0).SSID);
        }
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult sr1, ScanResult sr2) {
                return Integer.compare(sr2.level, sr1.level);
            }
        });
        int n=3;
        if(results.toArray().length<3)
            n=results.toArray().length;
        int []floor={0,0,0,0,0,0};
        for(int i=0;i<n;i++)
        {
            String item=results.get(i).SSID;
            if (wifi2.contains(item)) {
                floor[2]++;
            }
            if (wifi3.contains(item)) {
                floor[3]++;
            }
            if (wifi4.contains(item)) {
                floor[4]++;
            }
            if (wifi5.contains(item)) {
                floor[5]++;
            }
        }

        for (int i = 1; i < floor.length; i++) {
            if (floor[i] > floor[curFloor]) {
                curFloor = i;
            }
        }
        Toast.makeText(this,"CURRENT -FLOOR ="+ curFloor, Toast.LENGTH_SHORT).show();
        ScanResult bestSignal = null;
        for (ScanResult result : results) {
            if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
                bestSignal = result;
            }
        }
        if (bestSignal != null) {
            String ssid = bestSignal.SSID;
            String bssid = bestSignal.BSSID;
            int signalStrength = WifiManager.calculateSignalLevel(bestSignal.level, 100);
            String message = String.format("The nearest Wi-Fi network is %s (%s) with a signal strength of %d%%.", ssid, bssid, signalStrength);
            wifi_name.setText(message);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Wi-Fi scan
                wifiManager.startScan();
                scanSuccess();
            } else {
                // Permission denied, show an error message
                Toast.makeText(this, "Location permission required to scan for Wi-Fi networks", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isLocationEnabled();

        if(isLocationEnabled)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app requires location services to function properly. Please enable location services in your device settings.")
                .setCancelable(false)
                .setPositiveButton("Settings", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeLocations() {
        {
            String[] places = {"LH306", "LH308", "LH309", "LH309", "LH310", "LH311", "LH312", "EC Staffroom", "CS Staffroom", "Texas Instruments Lab"};
            Location node0 = new Location("Balcony", new ArrayList<String>(Arrays.asList("Balcony", places[4], places[1])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottomLeft, PlacePosition.topLeft)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 180f, null, null, null, null, null);
            Location node1 = new Location("LH212", new ArrayList<String>(Arrays.asList(places[6], places[8])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomLeft, PlacePosition.right)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 0f, null, null, null, null, null);
            Location node2 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[5], places[8])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomRight, PlacePosition.bottomLeft)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 0f, null, null, null, null, null);
            Location node3 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[3], "Washroom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomRight, PlacePosition.right)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 0f, null, null, null, null, null);
            Location node4 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[9], places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.right, PlacePosition.bottomRight)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 45f, null, null, null, null, null);
            Location node5 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[0], places[7])), new ArrayList<Integer>(Arrays.asList(PlacePosition.left, PlacePosition.bottomRight)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 3, false, 135f, null, null, null, null, null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(), getImageResourceId(getApplicationContext(), "stairs.jpg"), 3, true, 0f, null, null, null, null, null);
            Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), getImageResourceId(getApplicationContext(), "stairs.jpg"), 3, true, 0f, null, null, null, null, null);
            makeConnections(node0,node1,node2,node3,node4,node5,stairs1,stairs2);
            LevelPointer.levels[3] = node0;
        }
        {
            String []places = {"LH301", "LH302", "BTL10", "BTL07", "BT HOD Cabin", "LH210", "LH211", "LH212", "Dept of Physical Education", "BT Staffroom", "Biokinetics Lab", "Instrumentation and Project Lab"};
            Location node0 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[5], places[8])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomLeft, PlacePosition.topRight)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 180f, null, null, null, null, null);
            Location node1 = new Location("LH212", new ArrayList<String>(Arrays.asList(places[7], "StaffRoom", "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomLeft, PlacePosition.right, PlacePosition.bottomRight)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 0f, null, null, null, null, null);
            Location node2 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[6], "StaffRoom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomRight, PlacePosition.left)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 0f, null, null, null, null, null);
            Location node3 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[4], places[10], "Washroom")), new ArrayList<Integer>(Arrays.asList(PlacePosition.topRight, PlacePosition.bottomRight, PlacePosition.right)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 45f, null, null, null, null, null);
            Location node4 = new Location("Balcony", new ArrayList<String>(Arrays.asList(places[11], places[2], places[3])), new ArrayList<Integer>(Arrays.asList(PlacePosition.bottomRight, PlacePosition.topLeft, PlacePosition.right)), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 135f, null, null, null, null, null);
            Location node5 = new Location("Balcony", new ArrayList<String>(), new ArrayList<Integer>(), getImageResourceId(getApplicationContext(), "LH211.jpg"), 2, false, 0f, null, null, null, null, null);
            Location stairs1 = new Location("Stairs1", new ArrayList<>(), new ArrayList<>(), getImageResourceId(getApplicationContext(), "stairs.jpg"), 2, true, 0f, null, null, null, null, null);
            Location stairs2 = new Location("Stairs2", new ArrayList<>(), new ArrayList<>(), getImageResourceId(getApplicationContext(), "stairs.jpg"), 2, true, 0f, null, null, null, null, null);

            makeConnections(node0,node1,node2,node3,node4,node5,stairs1,stairs2);

            LevelPointer.levels[2] = node0;
        }

        //connecting stairs
        LevelPointer.levels[2].getStairs().setStairs(LevelPointer.levels[3].getStairs());
        LevelPointer.levels[2].setStairsAngle(-10);

        LevelPointer.levels[3].getStairs().setStairs(LevelPointer.levels[2].getStairs());
        LevelPointer.levels[3].setStairsAngle(10);

        LevelPointer.levels[2].getRight().getStairs().setStairs(LevelPointer.levels[3].getRight().getStairs());
        LevelPointer.levels[2].getRight().setStairsAngle(-10);

        LevelPointer.levels[3].getRight().getStairs().setStairs(LevelPointer.levels[2].getRight().getStairs());
        LevelPointer.levels[3].getRight().setStairsAngle(10);

    }
    public static int getImageResourceId(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    private void makeConnections(Location node0,Location node1,Location node2,Location node3,Location node4,Location node5,Location stairs1,Location stairs2){
        node0.setLeft(node1);
        node0.setRight(node5);
        node0.setBack(node3);

        node1.setRight(node0);
        node1.setBack(node2);

        node2.setFront(node1);
        node2.setRight(node3);

        node3.setFront(node0);
        node3.setLeft(node2);
        node3.setRight(node4);

        node4.setLeft(node3);
        node4.setFront(node5);

        node5.setBack(node4);
        node5.setLeft(node0);

        node0.setStairs(stairs1);
        node5.setStairs(stairs2);
        node4.setStairs(stairs2);

        stairs1.setFront(node0);

        stairs2.setLeft(node5);
    }

    private Location getLocation(String src,int level){
        Location node =LevelPointer.levels[level];

        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getLeft();
        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getBack();
        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getRight();

        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        node=node.getRight();

        if(node.getPlaces().indexOf(src)!=-1)
            return node;
        return node.getFront();

    }

    private void setRoute(Location src,Location desti){
        // if there are in same level
        if(src.getLevel()==desti.getLevel()){
            findSmallestRoute(src,desti);
            smallest.forEach(element->{
                element.setInRoute(true);
                Log.d("taggg",element.toString());
            });
        }
        else{

        }
    }

    private void findSmallestRoute(Location src,Location dest){

//        Log.d("sdjsd",src.getLeft()+" left");
//        Log.d("sdjsd",src.getRight()+" right");
//        Log.d("sdjsd",src.getFront()+" front");
//        Log.d("sdjsd",src.getBack()+" back");
        traverse.addLast(src);
        src.setInRoute(true);

        if(src==dest){
            if(smallest.size()>traverse.size())
            {
                smallest.clear();

                traverse.forEach(element->{
                    smallest.addFirst(element);
                });
            }
            src.setInRoute(false);
            traverse.removeLast();
            return;
        }

        if(src.getLeft()!=null && !src.getLeft().getInRoute())
            findSmallestRoute(src.getLeft(),dest);

        if(src.getRight()!=null && !src.getRight().getInRoute())
            findSmallestRoute(src.getRight(),dest);

        if(src.getBack()!=null && !src.getBack().getInRoute())
            findSmallestRoute(src.getBack(),dest);

        if(src.getFront()!=null && !src.getFront().getInRoute())
            findSmallestRoute(src.getFront(),dest);
        src.setInRoute(false);
        traverse.removeLast();
        return;
    }




    private void resetInRoute(){
        int []arr={2,3};
        traverse.clear();
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        smallest.addFirst(null);
        for(int i=0;i<arr.length;i++){
            Location node=LevelPointer.levels[i];
            if(node==null)
                continue;
            node.setInRoute(false);
            node.getBack().setInRoute(false);
            node.getLeft().setInRoute(false);
            node.getRight().setInRoute(false);
            node.getStairs().setInRoute(false);
            node.getRight().getBack().setInRoute(false);
            node.getRight().getStairs().setInRoute(false);
            node.getLeft().getBack().setInRoute(false);
        }

    }
}
