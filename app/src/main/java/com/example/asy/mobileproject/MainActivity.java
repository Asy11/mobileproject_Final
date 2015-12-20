package com.example.asy.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    TextView textView;  //현재 좌표를 표시해주는 TextView
    TextView textView2; //현재 시간을 표시해주는 TextView
    EditText editText;  //한일을 저장할때 사용할 EditText
    EditText editText2; //분류 항목을 저장할때 사용할 EditText
    Button bt_save;     //자료를 저장할때 사용할 Button
    Button bt_move;     //Activity를 전환할때 사용할 Button
    double lng;         //현재 좌표의 Lng를 저장
    double lat;         //현재 좌표의 Lat를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);

        //textView 를 좌표와
        textView = (TextView) findViewById(R.id.log);
        textView.setText("GPS 가 잡혀야 좌표가 구해짐");

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("현재 날짜 및 시각 : " + getDateString());

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //현재 좌표(lat와 lng)를 나타내줌
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                textView.setText(lat + "," + lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        //자료를 가지고 DataActivity로 이동하는 Intent
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = textView.getText().toString() + "," + editText.getText().toString();
                intent = new Intent(MainActivity.this, DataActivity.class);
                intent.putExtra("TextIn", inputString);
                intent.putExtra("DateAndTime", getDateString());
                intent.putExtra("category", editText2.getText().toString());
                startActivity(intent);
            }
        });

        //DataActivity로 이동하는 Intent
        bt_move = (Button) findViewById(R.id.bt_move);
        bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, DataActivity.class);
                startActivity(intent);
            }
        });
    }

    //자료 저장 시점의 "년-월-일 시간:분" 을 리턴
    public String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }
}
