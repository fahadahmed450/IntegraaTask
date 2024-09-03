package com.fahad.integraatask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView lblMsg, lblForgotPass;
    EditText txtUserName, txtPass;
    Button btnLogin;
    ProgressDialog pd;
    DBConnection db;
    CheckBox chkRembrMe;

    private ApiInterface apiInterface;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        lblForgotPass = (TextView) findViewById(R.id.lblForgotPass);
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtPass = (EditText) findViewById(R.id.txtPass);
        chkRembrMe = (CheckBox) findViewById(R.id.chkRembrMe);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        db = new DBConnection(LoginActivity.this);

        apiInterface= RetrofitClient.getClient(ApiInterface.BASE_URL)
                .create(ApiInterface.class);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(txtUserName.getText().toString(), txtPass.getText().toString());
            }
        });

    }

    private void login(String username, String password) {
        pd = new ProgressDialog(LoginActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Logging In!\nPlease Wait...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();


        Call<LoginResponse> call = apiInterface.login(username, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String userID = response.body().getUserid();
                    String trackingToken = response.body().getTrackingToken();
                    String type = response.body().getType();

                    Core.setUserName(userID);

                    Cursor cursor = db.GetData("SELECT * FROM "+db.TABLE_User+" WHERE ID=1");
                    if (cursor.getCount()>0){
                        boolean isUpdate = db.UpdateLoginDetail(userID,token,trackingToken,type);
                        if(isUpdate){
                        }
                    }else{
                        boolean isInsert = db.InsertLoginDetail(userID,token,trackingToken,type);
                        if(isInsert){
                        }
                    }

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);

                } else {
                    Core.ErrorAlert(LoginActivity.this,"Error","Login Failed!");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                pd.dismiss();
                Core.ErrorAlert(LoginActivity.this,"Error",t.getMessage());
            }
        });
    }
}