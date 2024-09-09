package com.fahad.integraatask;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    TextView txtPL;

    private ApiInterface apiInterface;

    /////////////////////////////////////////Bluetooth Section//////////////////////////////////////
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private BufferedInputStream inputStream;

    private static final UUID MY_UUID =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPL = (TextView)findViewById(R.id.txtFinalPayload);
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
                                .setTitle("Confirmation")
                                .setMessage("Are you sure, do you want to logout now?")
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
        }, 0, 10800000); // 3 hours

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

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }

            // Get the Bluetooth device
            String DEVICE_ADDRESS = "BF:98:35:C8:45:84";
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

            // Create Bluetooth connection
            createBluetoothConnection();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to validate string using regex
    private boolean validateWithRegex(String value, String regex) {
        return value.matches(regex);
    }



    // Function to validate the IP address
    public static boolean isValidIPAddress(String ipAddress) {
        String IP_ADDRESS_PATTERN = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
        Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);

        if (matcher.matches()) {
            String[] octets = ipAddress.split("\\.");
            for (String octet : octets) {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Process payload and replace placeholders with actual values and checksum
    private String processPayloadWithChecksum(String payload, Map<String, PermissionResponse.Parameter> parameters) {
        for (Map.Entry<String, PermissionResponse.Parameter> entry : parameters.entrySet()) {
            String placeholder = entry.getKey();
            PermissionResponse.Parameter parameter = entry.getValue();

            String lbl = parameter.getLabel();
            String val = parameter.getValue();
            String rgex = parameter.getRequired();

            try {
                switch (parameter.getType()) {
                    case "checksum":
                        // Calculate checksum
                        String checksum = calculateChecksum(payload);
                        payload = payload.replace("{" + placeholder + "}", checksum);
                        break;

                    case "text":

                        String textValue = "";
                        if(lbl.equals("S/N")){
                            textValue = "12345678";
                            if (parameter.getRequired() != null && !textValue.matches(rgex)) {
                                showToast("Invalid value for " + parameter.getLabel());
                                continue;
                            }
                        } else if (lbl.equals("IP 1")) {
                            textValue = "192.168.1.6";
                            if (parameter.getRequired() != null && !textValue.matches(rgex)) {
                                showToast("Invalid value for " + parameter.getLabel());
                                continue;
                            }
                            textValue = TextToHex(textValue);
                        }
                        else if (lbl.equals("IP 2")) {
                            textValue = "192.168.1.10";
                            if (parameter.getRequired() != null && !textValue.matches(rgex)) {
                                showToast("Invalid value for " + parameter.getLabel());
                                continue;
                            }
                            textValue = TextToHex(textValue);
                        }


                        payload = payload.replace("{" + placeholder + "}", textValue); // Replace placeholder with actual value
                        break;

                    case "int":
                        //Checking Port
                        String portNo = "";

                        if(lbl.equals("Port 1")) {
                            portNo = "8080";
                        }
                        else if(lbl.equals("Port 2")) {
                            portNo = "8081";
                        }
                        if (parameter.getRequired() != null && !portNo.matches(rgex)) {
                            showToast("Invalid value for " + parameter.getLabel());
                            continue;
                        }
                        portNo = TextToHex(portNo);
                        payload = payload.replace("{" + placeholder + "}", portNo); // Replace placeholder with actual integer value
                        break;

//                    case "IP":
                        // Valid IP address
//                        if (parameter.getRequired() != null && !ip.matches(parameter.getRequired())) {
//                            showToast("Invalid IP for " + parameter.getLabel());
//                            continue;
//                        }

                        // Convert the IP address to hexadecimal
//                        String[] parts = ip.split("\\.");
//                        StringBuilder hexIp = new StringBuilder();
//                        for (String part : parts) {
//                            int decimal = Integer.parseInt(part);
//                            hexIp.append(String.format("%02X", decimal)); // Convert each part to 2-digit hex
//                        }
//                        payload = payload.replace("{" + placeholder + "}", hexIp.toString()); // Replace with hex equivalent of IP
//                        break;

                    default:
                        showToast("Unknown parameter type: " + parameter.getType());
                        continue; // Skip unknown parameter types
                }
            } catch (Exception e) {
                showToast("Error processing " + parameter.getLabel() + ": " + e.getMessage());
            }
        }
        return payload;
    }

    private String TextToHex(String textValue){
        String[] parts = textValue.split("\\.");
        StringBuilder hexIp = new StringBuilder();
        for (String part : parts) {
            int decimal = Integer.parseInt(part);
            hexIp.append(String.format("%02X", decimal)); // Convert each part to 2-digit hex
        }
        return hexIp.toString();
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    // Method to calculate checksum
    private String calculateChecksum(String payload) {
        int sum = 0;
        String cleanedPayload = payload.replaceAll("\\{[^}]*\\}", "00");

        for (int i = 0; i < cleanedPayload.length(); i += 2) {
            if (i + 2 <= cleanedPayload.length()) {
                String byteString = cleanedPayload.substring(i, i + 2);
                try {
                    int byteValue = Integer.parseInt(byteString, 16);
                    sum += byteValue;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid hex value: " + byteString);
                }
            }
        }

        int checksum = sum % 256;
        return String.format("%02X", checksum);
    }

    // Helper method to convert IP address to hex
//    private String convertIpToHex(String ipAddress) {
//        String[] parts = ipAddress.split("\\.");
//        StringBuilder hexIp = new StringBuilder();
//        for (String part : parts) {
//            int decimal = Integer.parseInt(part);
//            hexIp.append(String.format("%02X", decimal));
//        }
//        return hexIp.toString();
//    }

    void getPermissionRequest() {
        apiInterface = RetrofitClient.getClient(ApiInterface.BASE_URL).create(ApiInterface.class);

        Cursor curUser = db.GetData("SELECT * FROM " + db.TABLE_User + " WHERE ID=1");
        if (curUser.getCount() > 0) {
            String token = curUser.getString(2);
            Call<PermissionResponse> call = apiInterface.getPermission(token);
            call.enqueue(new Callback<PermissionResponse>() {
                @Override
                public void onResponse(Call<PermissionResponse> call, Response<PermissionResponse> response) {
                    PermissionResponse actionResponse = response.body();

                    for (PermissionResponse.Action action : actionResponse.getActions()) {
                        for (PermissionResponse.Action.Item item : action.getItems()) {
                            String payload = item.getPayload();

                            // Process the payload with checksum handling and regex validation
                            if (item.getParameters() != null) {
                                payload = processPayloadWithChecksum(payload, item.getParameters());
                            }


                            txtPL.append("\n\n"+payload);

                            System.out.println("Final Payload: " + payload);
                        }
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
        boolean isConnected = false;
        int retryCount = 0;

        while (!isConnected && retryCount < 3) {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }

                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = new BufferedInputStream(bluetoothSocket.getInputStream());
                isConnected = true; // Connection successful
                Toast.makeText(this, "Bluetooth connected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                retryCount++;
                if (retryCount >= 3) {
                    Toast.makeText(MainActivity.this, "Failed to connect after 3 attempts.", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private void sendData(String message) {
        if (outputStream != null && bluetoothSocket.isConnected()) {
            try {
                outputStream.write(message.getBytes());
                Toast.makeText(this, "Data sent", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Failed to send data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Bluetooth connection is closed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void readData() {
        if (inputStream != null && bluetoothSocket.isConnected()) {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    Toast.makeText(this, "Received: " + receivedMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Failed to read data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error closing socket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
