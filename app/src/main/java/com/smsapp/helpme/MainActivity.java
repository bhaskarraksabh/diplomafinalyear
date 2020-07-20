package com.smsapp.helpme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton bt;
    private FloatingActionButton bt1;
    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";
    private ListView detailsList;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);

        bt = findViewById(R.id.floatingActionButton4);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDetailsActivity.class);
                startActivity(intent);
            }
        });


        String[] columns = new String[]{
                DatabaseHelper.COL0,
                DatabaseHelper.COL1
        };
        int[] widgets = new int[]{
                R.id.details_id,
                R.id.phone_number
        };

        Cursor cursor = setupDetailsList();

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_view_details,
                    cursor, columns, widgets, 0);

        detailsList = (ListView) findViewById(R.id.list_view);
        detailsList.setAdapter(cursorAdapter);

        detailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.detailsList.getItemAtPosition(position);
                int detailsId = itemCursor.getInt(itemCursor.getColumnIndex(DatabaseHelper.COL0));
                Intent intent = new Intent(getApplicationContext(), EditorDeleteActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, detailsId);
                startActivity(intent);
            }
        });



    }

    private Cursor setupDetailsList() {
        final ArrayList<Details> detailsArrayList = new ArrayList<>();

        Cursor cursor = databaseHelper.getAllDetails();
        if (!cursor.moveToNext()) {
            Toast.makeText(this, "There are no records to show", Toast.LENGTH_SHORT).show();
        }

        return cursor;


    }


}






