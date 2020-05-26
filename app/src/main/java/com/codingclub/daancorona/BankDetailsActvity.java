package com.codingclub.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BankDetailsActvity extends AppCompatActivity {

    Button btnproceedBnk;
    EditText acc,ifsc;
    String acc_no,ifsc_no;
    LoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details_actvity);
        dialog=new LoadingDialog(this);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        String token=sharedPref.getString("Token","");


        final OkHttpClient httpClient = new OkHttpClient();

        Request request1 = new Request.Builder()
                .url("https://daancorona.tech/api/recipient_profile/")
                .addHeader("Authorization", "JWT " + token)
                .build();
        dialog.startloadingDialog();

        httpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dialog.dismissDialog();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                BankDetailsActvity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismissDialog();
                            JSONObject jsonObject=new JSONObject(response.body().string());
                            acc.setText(jsonObject.getString("account_no"));
                            ifsc.setText(jsonObject.getString("ifsc_code"));

                        } catch (JSONException | IOException e) {

                            BankDetailsActvity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                    Toast.makeText(BankDetailsActvity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            });

                            e.printStackTrace();
                        }
                    }
                });


            }
        });

        acc=findViewById(R.id.acc_no);
        ifsc=findViewById(R.id.ifsc);

        btnproceedBnk = findViewById(R.id.proceed_bank_details);


        if(sharedPref.getString("Lang","").equals("hin")){
            acc.setHint(getResources().getString(R.string.enteracc));
            ifsc.setHint(getResources().getString(R.string.ifsc));
            btnproceedBnk.setText(getResources().getString(R.string.proceed));
        }
        btnproceedBnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.startloadingDialog();
                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
                String token=sharedPref.getString("Token","");

                acc_no=acc.getText().toString().trim();
                ifsc_no=ifsc.getText().toString().trim();

                if(acc_no.isEmpty() | ifsc_no.isEmpty())
                    Toast.makeText(BankDetailsActvity.this,"Enter Details",Toast.LENGTH_SHORT).show();
                else{
                    final OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("account_no", acc_no)
                            .addEncoded("ifsc_code",ifsc_no)
                            .build();


                    Request request = new Request.Builder()
                            .url("https://daancorona.tech/api/recipient_profile/")
                            .addHeader("Authorization", "JWT " + token)
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            BankDetailsActvity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                    Toast.makeText(BankDetailsActvity.this,"Error!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {

                            BankDetailsActvity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismissDialog();
                                    Toast.makeText(BankDetailsActvity.this,"Success!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putBoolean("Page3",true);
                            editor.apply();

                            Intent i = new Intent(BankDetailsActvity.this, MOUActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                        }
                    });

                }

            }
        });
    }
}
