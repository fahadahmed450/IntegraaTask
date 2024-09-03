package com.fahad.integraatask;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Core {
    public static String UserName;
    public static String getUserName() { return UserName; }
    public static void setUserName(String userName) { UserName = userName; }


    public static void InformationAlert(Context cntx, String _title, String _message)
    {
        AlertDialog DialogBox = new AlertDialog.Builder(cntx)
                .setTitle(_title)
                .setMessage(_message)
                .setIcon(R.drawable.icon_info)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void ErrorAlert(Context cntx,String _title, String _message)
    {
        AlertDialog DialogBox = new AlertDialog.Builder(cntx)
                .setTitle(_title)
                .setMessage(_message)
                .setIcon(R.drawable.icon_error)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
