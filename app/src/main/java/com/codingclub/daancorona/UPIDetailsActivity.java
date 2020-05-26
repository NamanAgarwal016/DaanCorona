package com.codingclub.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UPIDetailsActivity extends AppCompatActivity {
    Button btnproceedUpi;
    LoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_p_i_details);

        SharedPreferences sharedPref;
        sharedPref=getSharedPreferences("User",MODE_PRIVATE);

        TextInputEditText upivpa=findViewById(R.id.upi_vpa);
        TextInputLayout upivpa1=findViewById(R.id.upi_vpa1);
        dialog=new LoadingDialog(this);

        btnproceedUpi = findViewById(R.id.proceed_upi_details);
        if(sharedPref.getString("Lang","").equals("hin")){
            btnproceedUpi.setText(getResources().getString(R.string.proceed));
            upivpa1.setHint(getResources().getString(R.string.entervpa));
        }

        btnproceedUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);

                String token=sharedPref.getString("Token","");
                String upi=upivpa.getText().toString();

                if(upi.isEmpty())
                    Toast.makeText(UPIDetailsActivity.this,"Enter Upi",Toast.LENGTH_SHORT).show();
                else {

                    dialog.startloadingDialog();
                    final OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("upi", upi)
                            .build();

                    Request request = new Request.Builder()
                            .url("https://daancorona.tech/api/recipient_profile/")
                            .addHeader("Authorization", "JWT " + token)
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            UPIDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                    Toast.makeText(UPIDetailsActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            UPIDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                    Toast.makeText(UPIDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.d("Resp",""+response);
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putBoolean("Page3",true);
                            editor.apply();
                            Intent i = new Intent(UPIDetailsActivity.this, MOUActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                }

            }
        });

        dialog.startloadingDialog();

        final OkHttpClient httpClient = new OkHttpClient();
        String token=sharedPref.getString("Token","");

        Request request = new Request.Builder()
                .url("https://daancorona.tech/api/recipient_profile/")
                .addHeader("Authorization", "JWT " + token)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dialog.dismissDialog();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                UPIDetailsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismissDialog();
                            JSONObject jsonObject=new JSONObject(response.body().string());
                            upivpa.setText(jsonObject.getString("upi"));

                        } catch (JSONException | IOException e) {

                            UPIDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                }
                            });

                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }
}
