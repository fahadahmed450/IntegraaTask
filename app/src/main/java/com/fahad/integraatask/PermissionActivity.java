package com.fahad.integraatask;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PermissionActivity extends AppCompatActivity {

    EditText etPayload;
    DBConnection db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etPayload = (EditText) findViewById(R.id.txtPayload);
        db = new DBConnection(PermissionActivity.this);

        Cursor cursor = db.GetData("SELECT * FROM " + db.TABLE_Common + " WHERE ID=1");
        if (cursor.getCount() > 0) {
            String payLd = cursor.getString(2);
            etPayload.setText(payLd);
        }
    }
}