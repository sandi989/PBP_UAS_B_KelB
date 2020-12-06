package com.laundry.laundry.ui.sepatu;

import android.os.Bundle;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.laundry.laundry.R;
import com.laundry.laundry.api.SepatuAPI;
import com.laundry.laundry.model.Sepatu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class UpdateSepatu extends Fragment {
    private View view;
    private TextInputEditText etKondisi, etJenisSepatu;
    private Button cancelBtn, updateBtn;
    private String tempJenisLayanan;
    private Sepatu sepatu;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_sepatu, container, false);

        initField();

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
                        RadioButton radioSuede = view.findViewById(R.id.radio_kilat);
                        tempJenisLayanan = radioSuede.getText().toString();
                        break;
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out // popExit
                        );
                fragmentTransaction.hide(UpdateSepatu.this).commit();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etKondisi.getText().toString().isEmpty()){
                    etKondisi.setError("Silakan isi dengan benar!");
                } else if (etJenisSepatu.getText().toString().isEmpty() ){
                    etJenisSepatu.setError("Silakan isi dengan benar!");
                } else if (tempJenisLayanan.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),"Silakan pilih Jenis Pakaian", Toast.LENGTH_LONG).show();
                } else {
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    StringRequest stringRequest = new StringRequest(POST, SepatuAPI.URL_UPDATE + sepatu.getStringId(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);

                                Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            closeFragment();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("kondisi", etKondisi.getText().toString());
                            params.put("jenis_sepatu", etJenisSepatu.getText().toString());
                            params.put("jenis_layanan", tempJenisLayanan);

                            return params;
                        }
                    };
                    queue.add(stringRequest);
                    closeFragment();
                }
            }
        });

        return view;
    }

    private void closeFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(UpdateSepatu.this).detach(this)
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out // popExit
                )
                .attach(this).commit();
    }

    private void initField() {
        sepatu = (Sepatu) getArguments().getSerializable("sepatu");

        etKondisi = view.findViewById(R.id.etKondisiSepatu);
        etJenisSepatu = view.findViewById(R.id.etJenisSepatu);
        radioGroup = view.findViewById(R.id.radioGroup_jenis);
        cancelBtn = view.findViewById(R.id.btnCancel);
        updateBtn = view.findViewById(R.id.btnUpdate);

        etKondisi.setText(String.valueOf(sepatu.getKondisi()));
        etJenisSepatu.setText(String.valueOf(sepatu.getJenis_sepatu()));

        if (sepatu.getJenis_layanan().equals("Biasa")){
            RadioButton radioBiasa = view.findViewById(R.id.radio_biasa);
            radioBiasa.setChecked(true);
            tempJenisLayanan = radioBiasa.getText().toString();
        } else {
            RadioButton radioKilat = view.findViewById(R.id.radio_kilat);
            radioKilat.setChecked(true);
            tempJenisLayanan = radioKilat.getText().toString();
        }
    }
}