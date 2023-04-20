package com.example.volleyselect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String URLSelect = "https://websensordata.000webhostapp.com/android/select.php";
    public static final String URLDelete = "https://websensordata.000webhostapp.com/android/delete.php";
    public static final String URLEdit = "https://websensordata.000webhostapp.com/android/edit.php";
    public static final String URLInsert = "https://websensordata.000webhostapp.com/android/insert.php";
    //public static final String URLSelect = "http://192.168.1.5:8080/kampus/android/select.php";
    //public static final String URLDelete = "http://192.168.1.5:8080/kampus/android/delete.php";

    ListView list;
    SwipeRefreshLayout swipe;
    List<Data> itemList = new ArrayList<Data>();
    MhsAdapter adapter;
    LayoutInflater inflater;
    EditText tid, tnim, tnama, talamat;
    String vid, vnim, vnama, valamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        list = (ListView) findViewById(R.id.list);

        adapter = new MhsAdapter(MainActivity.this, itemList);
        list.setAdapter(adapter);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           itemList.clear();
                           adapter.notifyDataSetChanged();
                           callVolley();
                       }
                   }
        );
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String idx = itemList.get(position).getId();
                final CharSequence[] pilihanAksi = {"Delete", "Edit"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setItems(pilihanAksi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case 0:
                                hapusData(idx);
                                break;
                            case 1:
                                ubahData(idx);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }

    public void ubahData(String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLEdit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);

                            String idx = jObj.getString("id");
                            String nimx = jObj.getString("nim");
                            String namax = jObj.getString("nama");
                            String alamatx = jObj.getString("alamat");

                            dialogForm(idx, nimx, namax, alamatx, "Update");

                            adapter.notifyDataSetChanged();

                        }catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Gagal Koneksi Server", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
    public void dialogForm(String id, String nim, String nama, String alamat, String button){
        AlertDialog.Builder dialogForm = new AlertDialog.Builder( MainActivity.this);
        inflater = getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.form_mahasiswa, null);
        dialogForm.setView(viewDialog);
        dialogForm.setCancelable(true);
        dialogForm.setTitle(" ");


        tid = (EditText) viewDialog.findViewById(R.id.inId);
        tnim = (EditText) viewDialog.findViewById(R.id.inNim);
        tnama = (EditText) viewDialog.findViewById(R.id.inNama);
        talamat = (EditText) viewDialog.findViewById(R.id.inAlamat);

        if (id.isEmpty()){
            tid.setText(null);
            tnim.setText(null);
            talamat.setText(null);
            tnama.setText(null);
        }else{
            tid.setText(id);
            tnim.setText(nim);
            tnama.setText(nama);
            talamat.setText(alamat);
        }

        dialogForm.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vid = tid.getText().toString();
                vnim = tnim.getText().toString();
                vnama = tnama.getText().toString();
                valamat = talamat.getText().toString();

                simpan();
                dialog.dismiss();
            }
        });
        dialogForm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tid.setText(null);
                tnim.setText(null);
                talamat.setText(null);
                tnama.setText(null);
            }
        });
        dialogForm.show();
    }

    public void simpan (){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLInsert,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callVolley();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "gagal koneksi ke server, cek setingan koneksi anda", Toast.LENGTH_LONG).show();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();

                if (vid.isEmpty()) {
                    params.put("nim", vnim);
                    params.put("nama", vnama);
                    params.put("alamat", valamat);
                    return params;
                }else{
                    params.put("id", vid);
                    params.put("nim", vnim);
                    params.put("nama", vnama);
                    params.put("alamat", valamat);
                    return params;
                }
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void hapusData(String id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLDelete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        callVolley();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Gagal Koneksi Server", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    public void onRefresh() {
        //  itemList.clear();
        //  adapter.notifyDataSetChanged();
        callVolley();
    }

    private void callVolley() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        // membuat request JSON
        JsonArrayRequest jArr = new JsonArrayRequest(URLSelect, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Data item = new Data();

                        item.setId(obj.getString("id"));
                        item.setNim(obj.getString("nim"));
                        item.setNama(obj.getString("nama"));
                        item.setAlamat(obj.getString("alamat"));

                        // menambah item ke array
                        itemList.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "gagal koneksi ke server, cek setingan koneksi anda", Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        });

        // menambah request ke request queue
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(jArr);

    }

}