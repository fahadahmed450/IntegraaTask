package com.fahad.integraatask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    DBConnection db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        db = new DBConnection(SplashActivity.this);

        File database = getApplicationContext().getDatabasePath(DBConnection.DBNAME);
        if (!database.exists()){
            db.getReadableDatabase();
            db.close();
            if (db.copyDatabase(SplashActivity.this)){
                Toast.makeText(SplashActivity.this,"copy database success",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(SplashActivity.this,"copy database error",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}