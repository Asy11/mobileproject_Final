package com.example.asy.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DataActivity extends AppCompatActivity {

    LatLng MyLocation;
    private GoogleMap map;

    TextView textView;      //db에 있는 모든 자료를 보여줄때 사용할 TextView
    EditText editText_remove;      //db에 있는 특정 자료를 삭제할때 사용할 EditText
    EditText editText_Category;    //db에 있는 특정 자료를 검색할때 사용할 EditText
    Button bt_exit;         //이전 엑티비티로 되돌아가는데 사용할 Button
    Button bt_remove;       //특정 자료를 삭제하는데 사용할 Button
    Button bt_statistics;
    Intent intent_statistics;

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // // Database 생성 및 열기
        db = openOrCreateDatabase(dbName, dbMode, null);
        // 테이블 생성
        createTable();

        //textView가 화면을 넘어가면 스크롤 가능하게 한다
        textView = (TextView) findViewById(R.id.editText);
        textView.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent(); //전달된 인텐트
        String textIn = intent.getStringExtra("TextIn");

        //이전 Activity로 되돌아감
        bt_exit = (Button) findViewById(R.id.bt_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText_remove = (EditText) findViewById(R.id.editText_remove);
        editText_Category = (EditText) findViewById(R.id.editText_category);

        //db의 특정 자료를 삭제 (db의 index를 이용)
        bt_remove = (Button) findViewById(R.id.bt_remove);
        bt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeData(Integer.parseInt(editText_remove.getText().toString()));
                    //자료 삭제 후 그 자료의 Marker도 함께 삭제해준다.
                    textView.setText(PrintData());
                    map.clear();
                    PrintGoogleMap();
                } catch (NumberFormatException e) {
                    //입력이 없거나 문자를 입력할 시에 나타날 Toast
                    Toast.makeText(DataActivity.this.getApplicationContext(), "입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //db의 특정 자료를 검색 (db의 category를 이용)
        bt_statistics = (Button) findViewById(R.id.bt_statistics);
        bt_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = CategoryData(editText_Category.getText().toString());
                intent_statistics = new Intent(DataActivity.this, Statistics.class);
                intent_statistics.putExtra("TextIn", inputString);
                startActivity(intent_statistics);
            }
        });

        //mainActivity에서 전달받은 내용을 활용하기 위한 try-catch 구문
        //(예외 처리를 안해주면 자료의 전달 없이 intent를 사용하면 에러남)
        try {
            String[] data = textIn.split(",");
            double lat = Double.parseDouble(data[0]);
            double lng = Double.parseDouble(data[1]);
            String text = data[2];
            String date = intent.getStringExtra("DateAndTime");
            String category = intent.getStringExtra("category");
            //Insert
            insertData(text, category, date, lat, lng);
        } catch (NullPointerException e) {
        }

        //db의 모든 자료를 표시함
        textView.setText(PrintData());

        //db의 자료를 토대로 googlemap을 표시함.
        PrintGoogleMap();

    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer primary key autoincrement, "
                    + "name text not null, "
                    + "date text not null, "
                    + "category text not null, "
                    + "lat double not null, "
                    + "lng double not null )";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    // Data 추가
    public void insertData(String name, String category, String date, double lat, double lng) {
        String sql = "insert into " + tableName + " values(NULL, '"
                + name + "'," + "'" + category + "'," + "'" + date + "'," + lat + ',' + lng + ");";
        db.execSQL(sql);
    }

    // Data 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }

    // 모든 Data 읽기
    public String PrintData() {
        String str = "";

        Cursor cursor = db.rawQuery("select * from idListTable", null);
        while (cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " ) 일 혹은 사건 : "
                    + cursor.getString(1)
                    + "  분류 : "
                    + cursor.getString(2)
                    + "\n"
                    + cursor.getString(3)
                    + "\n";
        }

        return str;
    }

    //특정 분류의 Data 읽기 (category를 통해 검색함)
    public String CategoryData(String category) {
        String str = "";
        int count = 0;

        Cursor cursor = db.rawQuery("select * from idListTable", null);
        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            String doing = cursor.getString(1);
            if (category.compareTo(cursor.getString(2)) == 0) {
                str += index
                        + " ) 일 혹은 사건 : "
                        + doing
                        + "\n"
                        + cursor.getString(3)
                        + "\n";
                count++;
            }
        }
        str += "\n\n분류 : '" + category + "'  에 해당하는 목록입니다.";
        str += "\n총 " + count + " 개 있습니다.";

        return str;
    }

    // 구글맵을 띄우고 Marker까지 찍는 메서드
    public void PrintGoogleMap() {

        Cursor cursor = db.rawQuery("select * from idListTable", null);
        while (cursor.moveToNext()) {
            int index = cursor.getInt(0);
            String doing = cursor.getString(1);
            cursor.getString(2);
            String date = cursor.getString(3);
            double lat = cursor.getDouble(4);
            double lng = cursor.getDouble(5);
            MyLocation = new LatLng(lat, lng);
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            Marker me = map.addMarker(new MarkerOptions().position(MyLocation).title(index + ") " + doing + "  " + date));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }

    }

}
