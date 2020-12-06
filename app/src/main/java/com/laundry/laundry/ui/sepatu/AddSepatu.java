package com.laundry.laundry.ui.sepatu;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.laundry.laundry.R;
import com.laundry.laundry.api.SepatuAPI;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class AddSepatu extends Fragment {
    private TextInputEditText edtJenisSepatu, edtKondisi;
    private Button cancelBtn, addBtn;
    private String tempJenisSepatu, tempKondisi, tempJenisLayanan="";
    private ProgressDialog progressDialog;

    public AddSepatu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sepatu, container, false);

        progressDialog = new ProgressDialog(getActivity());

        edtKondisi = view.findViewById(R.id.etKondisiSepatu);
        edtJenisSepatu = view.findViewById(R.id.etJenisSepatu);
        cancelBtn = view.findViewById(R.id.btnCancel);
        addBtn = view.findViewById(R.id.btnAdd);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_jenis);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_biasa:
                        RadioButton radioBiasa = view.findViewById(R.id.radio_biasa);
                        tempJenisLayanan = radioBiasa.getText().toString();
                        break;
                    case R.id.radio_kilat:
                        RadioButton radioKilat = view.findViewById(R.id.radio_kilat);
                        tempJenisLayanan = radioKilat.getText().toString();
                        break;
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFragment();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempKondisi = edtKondisi.getText().toString();
                tempJenisSepatu = edtJenisSepatu.getText().toString();

                if(tempKondisi.isEmpty()){
                    edtKondisi.setError("Silakan diisi dengan benar");}
                if(tempJenisSepatu.isEmpty()){
                    edtJenisSepatu.setError("Silakan diisi dengan benar");}
                if (tempJenisLayanan.length()==0){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Silakan pilih jenis layanan", Toast.LENGTH_SHORT).show();
                }
                if(!tempKondisi.isEmpty() && !tempJenisSepatu.isEmpty() && tempJenisLayanan.length()>0) {

                    progressDialog.show();

                    addSepatu();

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out // popExit
                            );
                    fragmentTransaction.hide(AddSepatu.this).addToBackStack(null).commit();
                }
            }
        });
    }

    private void addSepatu() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(POST, SepatuAPI.URL_ADD, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);

                    Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("jenis_layanan", tempJenisLayanan);
                params.put("kondisi", tempKondisi);
                params.put("jenis_sepatu", tempJenisSepatu);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void closeFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(AddSepatu.this).detach(this)
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out) // popExit
                .attach(this).commit();
    }
}