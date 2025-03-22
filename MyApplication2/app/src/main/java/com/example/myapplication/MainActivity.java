package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.database.DataBase;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.fragments.CardOfCar;
import com.example.myapplication.fragments.MapFragment;
import com.example.myapplication.fragments.ProfileFragment;
import com.example.myapplication.fragments.SearchFragment;
import com.google.android.material.tabs.TabLayout;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CameraListener {
    public static final String APP_PREFERENCES = "myProfile";
    public static final String APP_PREFERENCES_IS_RENT = "IsRent";
    public static final String APP_PREFERENCES_ID_RENT_CAR = "IDRentCar";
    SharedPreferences sPref;
    MapObjectCollection mapObjectCollection;
    MapObjectTapListener mapObjectTapListener;
    public TabLayout tabLayout;
    private MapView mapView;
    ImageButton btnFindMyLocation, btnPlus, btnMinus, btnCar;
    DataBase db;
    List<PointList> pointList = new ArrayList<>();
    PlacemarkMapObject[] placemarkMapObjects;
    Point[] points;
    String[] idMarker;
    HashMap<PlacemarkMapObject, String> mHashMap = new HashMap<PlacemarkMapObject, String>();
    Point locationNow;
    float zoom = 14.0f;

    int sizeCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("b942327f-f9dd-4079-912e-2f837f5e0e99");
        MapKitFactory.initialize(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sPref = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        db = new DataBase(this);

        tabLayout = findViewById(R.id.tab_layout);
        mapView = findViewById(R.id.mapview);
        btnFindMyLocation = findViewById(R.id.btnFindLocation);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnCar = findViewById(R.id.btnFindLocationCar);

        MapKit mapKit = MapKitFactory.getInstance();
        MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.FCV_tab, MapFragment.newInstance()).commit();

        mapView.getMap().addCameraListener(this);

        locationNow = mapView.getMap().getCameraPosition().getTarget();

        ResetMarkers();
        StoreDataInPoints();
        setLatAndLong();
        setMarkerInStartLocation();

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMap().move(new CameraPosition(locationNow, zoom + 1.0f, 0f, 0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);

                zoom += 1.0f;
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMap().move(new CameraPosition(locationNow, zoom - 1.0f, 0f, 0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);

                zoom -= 1.0f;
            }
        });
        btnFindMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation(mapKit);
            }
        });
        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapMarkerAndOpenData(fragmentManager, sPref.getString(APP_PREFERENCES_ID_RENT_CAR, "NULL"));
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ResetMarkers();
                if(tab.getPosition() == 0)
                    fragmentManager.beginTransaction().replace(R.id.FCV_tab, MapFragment.newInstance()).commit();
                else if(tab.getPosition() == 1)
                    fragmentManager.beginTransaction().replace(R.id.FCV_tab, SearchFragment.newInstance()).commit();
                else if(tab.getPosition() == 2)
                    fragmentManager.beginTransaction().replace(R.id.FCV_tab, ProfileFragment.newInstance()).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                tapMarkerAndOpenData(fragmentManager, mHashMap.get(mapObject));
                return true;
            }
        };

        for(int i = 0; i < sizeCar;i++)
            placemarkMapObjects[i].addTapListener(mapObjectTapListener);

        if(sPref.getString(APP_PREFERENCES_IS_RENT, "NULL").equals("true"))
            tapMarkerAndOpenData(fragmentManager, sPref.getString(APP_PREFERENCES_ID_RENT_CAR, "NULL"));
        else
            findLocation(mapKit);

        checkIdForBtn();
    }

    public void checkIdForBtn()
    {
        if(sPref.getString(APP_PREFERENCES_IS_RENT, "NULL").contains("false"))
        {
            int color = Color.parseColor("#808080");
            btnCar.setEnabled(false);
            btnCar.setBackgroundTintList(ColorStateList.valueOf(color));
        }
        else
        {
            int color = Color.parseColor("#ECECEC");
            btnCar.setEnabled(true);
            btnCar.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    public void tapMarkerAndOpenData(FragmentManager fragmentManager, String id)
    {
        ResetMarkers();
        ZoomToMarker(id);
        Cursor cursor = db.readAllData(true, id);
        if(cursor.getCount() == 1){

            Bundle bundle = new Bundle();
            bundle.putString("auto_id", cursor.getString(0));
            bundle.putString("auto_name", cursor.getString(1));
            bundle.putString("auto_number", cursor.getString(2));
            bundle.putString("auto_gaz", cursor.getString(3));
            bundle.putString("auto_status", cursor.getString(4));
            bundle.putString("auto_price", cursor.getString(5));
            bundle.putString("auto_imageBIG", cursor.getString(7));

            fragmentManager.beginTransaction().replace(R.id.FCV_tab, CardOfCar.newInstance(bundle)).commit();
        }
    }

    public void ResetMarkers()
    {
        int marker2 = R.drawable.ic_pin_black_png;
        for(PlacemarkMapObject placemarkMapObject : mHashMap.keySet())
            placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker2));
    }

    public void ZoomToMarker(String id)
    {
        int marker = R.drawable.ic_pin_yellow_png;
        for(PlacemarkMapObject placemarkMapObject : mHashMap.keySet())
        {
            if(mHashMap.get(placemarkMapObject).contains(id))
            {
                placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker));
                mapView.getMap().move(new CameraPosition(placemarkMapObject.getGeometry(), 15.0f, 0f, 0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
            }
        }
    }

    //CHECK LOCATION PHONE
    public void findLocation(MapKit mapKit)
    {

        mapKit.createLocationManager().requestSingleUpdate(new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                locationNow = location.getPosition();
                mapView.getMap().move(new CameraPosition(location.getPosition(), 15.0f, 0f, 0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
                zoom = 15.0f;
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {

            }
        });
    }

    // START AND STOP MAP
    @Override
    public void onStart()
    {
        super.onStart();

        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        MapKitFactory.getInstance().onStop();
        mapView.onStop();
    }

    //CHANGE CAMERA POSITION
    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {
        if(b)
        {
            locationNow = mapView.getMap().getCameraPosition().getTarget();
            zoom = mapView.getMap().getCameraPosition().getZoom();
        }
    }

    //SET MARKERS
    public void setMarkerInStartLocation()
    {
        int marker = R.drawable.ic_pin_black_png;
        mapObjectCollection = mapView.getMap().getMapObjects();
        placemarkMapObjects = new PlacemarkMapObject[sizeCar];
        if(sizeCar != 0)
        {
            for(int i = 0; i < sizeCar ;i++)
            {
                placemarkMapObjects[i] = mapObjectCollection.addPlacemark(points[i], ImageProvider.fromResource(this, marker));
                mHashMap.put(placemarkMapObjects[i], idMarker[i]);
            }
        }
    }

    public void setLatAndLong()
    {
        idMarker = new String[sizeCar];
        points = new Point[sizeCar];
        for(int i = 0; i < sizeCar; i++)
        {
            idMarker[i] = pointList.get(i).getAuto_id();
            points[i] = new Point(pointList.get(i).getLatitude() ,
                    pointList.get(i).getLongitude());
        }
    }

    private void StoreDataInPoints() {
        Cursor cursor = db.getLatAndLong();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Нет данных", Toast.LENGTH_LONG).show();

        }
        else{
            while(cursor.moveToNext()){
                pointList.add(new PointList(cursor.getString(0),
                        cursor.getFloat(8),
                        cursor.getFloat(9)));
            }
        }

        sizeCar = pointList.size();
    }

    public void setStatusCar(String id, String status)
    {
        db.setDataCar(id, status);
    }
}