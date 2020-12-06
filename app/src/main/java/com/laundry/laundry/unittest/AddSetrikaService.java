package com.laundry.laundry.unittest;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.laundry.laundry.R;
import com.laundry.laundry.api.SetrikaAPI;
import com.laundry.laundry.model.Setrika;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class AddSetrikaService {

    public void addsetrika(final AddSetrikaView view, double berat, int jumlah, String jenis, final AddSetrikaCallback callback){

        RequestQueue queue = Volley.newRequestQueue((Context) view);

        StringRequest stringRequest = new StringRequest(POST, SetrikaAPI.URL_ADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.showErrorResponse(error.getMessage());
                callback.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("berat", String.valueOf(berat));
                params.put("jumlah_pakaian", String.valueOf(jumlah));
                params.put("jenis_pakaian", jenis);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public Boolean getValid(final AddSetrikaView view, double berat, int jumlah, String jenis){
        final Boolean[] bool = new Boolean[1];
        addsetrika(view, berat, jumlah, jenis, new AddSetrikaCallback() {
            @Override
            public void onSuccess(boolean value, Setrika setrika) {
                bool[0] = true;
            }

            @Override
            public void onError() {
                bool[0] = false;
            }
        });
        return bool[0];
    }

}
