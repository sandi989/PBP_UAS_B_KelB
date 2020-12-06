package com.laundry.laundry.ui.setrika;
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
import com.laundry.laundry.api.SetrikaAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class AddSetrika extends Fragment {
    private TextInputEditText edtJumlah, edtBerat;
    private Button cancelBtn, addBtn;
    private String tempJumlah, tempBerat, tempJenis="";
    private int jumlah;
    private double berat;

    public AddSetrika() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_setrika, container, false);

        edtJumlah = view.findViewById(R.id.etJumlah);
        edtBerat = view.findViewById(R.id.etBerat);
        cancelBtn = view.findViewById(R.id.btnCancel);
        addBtn = view.findViewById(R.id.btnAdd);

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
                tempJumlah = edtJumlah.getText().toString();
                tempBerat = edtBerat.getText().toString();

                if(tempJumlah.isEmpty()){
                    edtJumlah.setError("Silakan diisi dengan benar");}
                if(tempBerat.isEmpty()){
                    edtBerat.setError("Silakan diisi dengan benar");}
                if (tempJenis.length()==0){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Silakan pilih jenis pakaian", Toast.LENGTH_SHORT).show();
                }
                if(!tempJumlah.isEmpty() && !tempBerat.isEmpty() && tempJenis.length()>0) {
                    jumlah = Integer.parseInt(tempJumlah);
                    berat = Double.parseDouble(tempBerat);

                    addSetrika();

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out // popExit
                            );
                    fragmentTransaction.hide(AddSetrika.this).addToBackStack(null).commit();
                }
            }
        });
    }

    public void addSetrika(){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(POST, SetrikaAPI.URL_ADD, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                closeFragment();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("berat", tempBerat);
                params.put("jumlah_pakaian", tempJumlah);
                params.put("jenis_pakaian", tempJenis);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void closeFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(AddSetrika.this).detach(this)
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out) // popExit
                .attach(this).commit();
    }
}