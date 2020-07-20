package com.smsapp.helpme;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditorDeleteActivity extends AppCompatActivity implements View.OnClickListener {

    EditText pswdEditText, mpswdReApply;
    EditText mPhoneNumber,paswd_display;
    CheckBox mIsDefault;
    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton;
    int mdetailId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mdetailId = getIntent().getIntExtra(MainActivity.KEY_EXTRA_CONTACT_ID, 0);

        setContentView(R.layout.activity_editor_delete);
        pswdEditText = (EditText) findViewById(R.id.edit_spassword);
        mpswdReApply = (EditText) findViewById(R.id.edit_retype);
        mIsDefault = (CheckBox) findViewById(R.id.checkBox);

        saveButton = (Button) findViewById(R.id.edit_saveButton);
        saveButton.setOnClickListener(this);

        deleteButton = (Button) findViewById(R.id.edit_delete);
        deleteButton.setOnClickListener(this);

        mPhoneNumber = (EditText) findViewById(R.id.edit_phonenumber);
        paswd_display=(EditText) findViewById(R.id.paswd_display);

        dbHelper = new DatabaseHelper(this);

        if (mdetailId > 0) {

            Cursor rs = dbHelper.getRecordById(mdetailId);
            rs.moveToFirst();
            String phoneNumber = rs.getString(rs.getColumnIndex(DatabaseHelper.COL1));
            String isDefault = rs.getString(rs.getColumnIndex(DatabaseHelper.COL4));
            paswd_display.setText(rs.getString(rs.getColumnIndex(DatabaseHelper.COL2)));
            if (!rs.isClosed()) {
                rs.close();
            }

            if (isDefault.equals("1"))
                mIsDefault.setChecked(true);

            pswdEditText.setFocusable(true);
            pswdEditText.setClickable(true);


            mpswdReApply.setFocusable(true);
            mpswdReApply.setClickable(true);

            mPhoneNumber.setText(phoneNumber);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_saveButton:
                saveDetails();
                return;

            case R.id.edit_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               int res= dbHelper.deleteRecord(mdetailId);
                               String message="Deleted Successfully";
                               if(res!=1){
                                   message="Error deleting record";
                               }
                                Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Record?");
                d.show();
                return;
        }
    }

    public void saveDetails() {
        if (mdetailId > 0) {

            if (mIsDefault.isChecked())
                dbHelper.updateDetailsToDefault(mdetailId, "1",pswdEditText.getText().toString());
            else
                dbHelper.updateDetailsToDefault(mdetailId, "0",pswdEditText.getText().toString());


            Toast.makeText(getApplicationContext(), "Person Update Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        }
    }
}
