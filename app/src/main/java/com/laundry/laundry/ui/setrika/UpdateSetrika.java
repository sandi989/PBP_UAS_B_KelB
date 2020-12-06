package com.laundry.laundry.ui.setrika;

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
import com.laundry.laundry.api.SetrikaAPI;
import com.laundry.laundry.model.Setrika;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class UpdateSetrika extends Fragment {
    private View view;
    private TextInputEditText etJumlah, etBerat;
    private Button cancelBtn, updateBtn;
    private String tempJenis;
    private Setrika setrika;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_setrika, container, false);

        initField();

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_jenis);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_biasa:
                        RadioButton radioBiasa = view.findViewById(R.id.radio_biasa);
                        tempJenis = radioBiasa.getText().toString();
                        break;
                    case R.id.radio_suede:
                        RadioButton radioSuede = view.findViewById(R.id.radio_suede);
                        tempJenis = radioSuede.getText().toString();
                        break;
                    case R.id.radio_jersey:
                        RadioButton radioJersey = view.findViewById(R.id.radio_jersey);
                        tempJenis = radioJersey.getText().toString();
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
                fragmentTransaction.hide(UpdateSetrika.this).commit();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etJumlah.getText().toString().isEmpty()){
                    etJumlah.setError("Silakan isi dengan benar!");
                } else if (etBerat.getText().toString().isEmpty() ){
                    etBerat.setError("Silakan isi dengan benar!");
                } else if (tempJenis.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),"Silakan pilih Jenis Pakaian", Toast.LENGTH_LONG).show();
                } else {
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    StringRequest stringRequest = new StringRequest(POST, SetrikaAPI.URL_UPDATE + setrika.getStringId(), new Response.Listener<String>() {
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
                            params.put("berat", etBerat.getText().toString());
                            params.put("jumlah_pakaian", etJumlah.getText().toString());
                            params.put("jenis_pakaian", tempJenis);

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

    private void initField() {
        setrika = (Setrika) getArguments().getSerializable("setrika");

        etBerat = view.findViewById(R.id.etBerat);
        etJumlah = view.findViewById(R.id.etJumlah);
        radioGroup = view.findViewById(R.id.radioGroup_jenis);
        cancelBtn = view.findViewById(R.id.btnCancel);
        updateBtn = view.findViewById(R.id.btnUpdate);

        etBerat.setText(String.valueOf(setrika.getBerat()));
        etJumlah.setText(String.valueOf(setrika.getJumlah_pakaian()));

        if (setrika.getJenis_pakaian().equals("Biasa")){
            RadioButton radioBiasa = view.findViewById(R.id.radio_biasa);
            radioBiasa.setChecked(true);
            tempJenis = radioBiasa.getText().toString();
        }else if (setrika.getJenis_pakaian().equals("Suede")) {
            RadioButton radioSuede = view.findViewById(R.id.radio_suede);
            radioSuede.setChecked(true);
            tempJenis = radioSuede.getText().toString();
        } else {
            RadioButton radioJersey = view.findViewById(R.id.radio_jersey);
            radioJersey.setChecked(true);
            tempJenis = radioJersey.getText().toString();
        }
    }

    public void closeFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(UpdateSetrika.this).detach(this)
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out // popExit
                )
                .attach(this).commit();
    }

}