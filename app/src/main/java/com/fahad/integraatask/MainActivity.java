package com.fahad.integraatask;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    NavigationView navigationView;
    Button btnOpen;
    DBConnection db;

    private ApiInterface apiInterface;


    /////////////////////////////////////////Bluetooth Section//////////////////////////////////////
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private static final UUID MY_UUID =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        db = new DBConnection(MainActivity.this);
        btnOpen = (Button) findViewById(R.id.btnOpen);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_lblLoggedUser).setTitle("Logged in as : " + Core.getUserName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logoutUser:
                        drawerLayout.closeDrawers();
                        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(MainActivity.this)
                                //set message, title, and icon
                                .setTitle("Confirmation")
                                .setMessage("Are you sure, do you want to logout now?")
                                .setIcon(R.drawable.icon_confirmation)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        break;

                    case R.id.nav_bluetooth:

                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentOpenBluetoothSettings);

                        break;

                    case R.id.nav_Common:
                        Intent i = new Intent(MainActivity.this, PermissionActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_Network:

                        break;

                }
                return false;
            }
        });


        Timer timerChk = new Timer();
        timerChk.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPermissionRequest();
                    }
                });
            }
        }, 0, 10800000); //3 h = 10800000 ms

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("Hello from Bluetooth!");
            }
        });

        /////////////////////////////////////////Bluetooth Section//////////////////////////////////////
        // Initialize Bluetooth adapter
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // Ensure Bluetooth is enabled
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    return;
                }
            }

            // Get the Bluetooth device (example: use paired devices list or device name)
            String DEVICE_ADDRESS = "BF:98:35:C8:45:84";
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

            // Create connection to the Bluetooth device
            createBluetoothConnection();
        }catch (Exception e){

        }
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    void getPermissionRequest() {
        apiInterface = RetrofitClient.getClient(ApiInterface.BASE_URL).create(ApiInterface.class);

        Cursor curUser = db.GetData("SELECT * FROM " + db.TABLE_User + " WHERE ID=1");
        if (curUser.getCount() > 0) {
            String token = curUser.getString(2);
            Call<PermissionResponse> call = apiInterface.getPermission(token);
            call.enqueue(new Callback<PermissionResponse>() {
                @Override
                public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> response) {
                    //if response is 200
                    if (response.isSuccessful()) {
                        PermissionResponse permissionResponse = response.body();
                        if (permissionResponse != null) {

//                            String label = permissionResponse.getActions().getCommon().getItems().getOpen().getLabel();
//                            String permission = permissionResponse.getActions().getCommon().getItems().getOpen().getPayload();
//                            // navigate through the nested structures and access the data
//
//                            //Save waterPermission info in
//                            Cursor cursor = db.GetData("SELECT * FROM " + db.TABLE_Common + " WHERE ID=1");
//                            if (cursor.getCount() > 0) {
//                                boolean isUpdate = db.UpdateCommonPermission(label, permission);
//                                if (isUpdate) {
//                                }
//                            } else {
//                                boolean isInsert = db.InsertCommonPermission(label, permission);
//                                if (isInsert) {
//                                }
//                            }
                        }
                    } else {
                        //response is not 200 and then logout
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PermissionResponse> call, Throwable t) {
                    Core.ErrorAlert(MainActivity.this, "Error", t.getMessage());
                }
            });
        }


    }

    /////////////////////////////////////////Bluetooth Section//////////////////////////////////////

    private void createBluetoothConnection() {
        try {
            // Create an RFCOMM (SPP) connection
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(String message) {
        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes());
                Toast.makeText(this, "Data sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
}