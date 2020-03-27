package com.Faresa.POS.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Faresa.POS.Helper.SQLiteHandler;
import com.Faresa.POS.Setting.AppConfig;
import com.Faresa.pos.R;
import com.Faresa.POS.Setting.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    EditText nama,hp,email,alamat, pass_lama, pass_baru, konfirm_pass;
    String id_kasir;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        nama = findViewById(R.id.nama);
        hp = findViewById(R.id.hp);
        email = findViewById(R.id.email);
        alamat =findViewById(R.id.alamat);
        pass_lama = findViewById(R.id.pass_lama);
        pass_baru = findViewById(R.id.pass_baru);
        konfirm_pass = findViewById(R.id.konfirm_pass);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(5);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button update_profil = findViewById(R.id.update_profil);
        Button update_password = findViewById(R.id.update_password);

        // SQLite database handler
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        id_kasir = p.get("id_kasir");
        LoadKasir(id_kasir);

        update_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateKasir(id_kasir);
            }
        });

        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = pass_baru.getText().toString();
                String y = konfirm_pass.getText().toString();
                String z = pass_lama.getText().toString();
                if(x.equalsIgnoreCase("") || y.equalsIgnoreCase("") || z.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Lengkapi data password",Toast.LENGTH_LONG).show();
                }else {
                    if (x.equalsIgnoreCase(y)) {
                        UpdatePassword(id_kasir);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password baru dan konfirmasi password tidak sama", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadKasir(final String id_kasir) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.DATA_KASIR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject kasir = jObj.getJSONObject("kasir");
                        nama.setText(kasir.getString("nama"));
                        hp.setText(kasir.getString("hp"));
                        email.setText(kasir.getString("email"));
                        alamat.setText(kasir.getString("alamat"));

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "ini error : " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage() + "zzzz", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id_kasir",id_kasir);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void UpdateKasir(final String id_kasir) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Sedang mengirim data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.UPDATE_KASIR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(),"Data berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "ini error : " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage() + "zzzz", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id_kasir",id_kasir);
                params.put("nama",nama.getText().toString());
                params.put("hp",hp.getText().toString());
                params.put("email",email.getText().toString());
                params.put("alamat",alamat.getText().toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void UpdatePassword(final String id_kasir) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Sedang mengirim data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.UPDATE_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(),"Password berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(), "Password lama tidak sesuai", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error 1: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage() + "zzzz", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id_kasir",id_kasir);
                params.put("pass_lama",pass_lama.getText().toString());
                params.put("pass_baru",pass_baru.getText().toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
