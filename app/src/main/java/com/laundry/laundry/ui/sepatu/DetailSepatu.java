package com.laundry.laundry.ui.sepatu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.laundry.laundry.R;
import com.laundry.laundry.api.SepatuAPI;
import com.laundry.laundry.model.Sepatu;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.POST;

public class DetailSepatu extends DialogFragment {
    private TextView tvId, tvJenisLayanan, tvKondisi, tvJenisSepatu;
    private String sIdSepatu, sJenisLayanan, sKondisi, sJenisSepatu;
    private ImageButton ibClose;
    private ProgressDialog progressDialog;
    private MaterialButton btnDelete, btnUpdate;
    private View view;
    private Sepatu sepatu;

    public static DetailSepatu newInstance(){ return new DetailSepatu(); }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_sepatu, container, false);

        sepatu = (Sepatu) getArguments().getSerializable("sepatu");

        ibClose = view.findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RequestQueue queue = Volley.newRequestQueue(view.getContext());

                                StringRequest stringRequest = new StringRequest(POST, SepatuAPI.URL_DELETE + sepatu.getStringId(), new com.android.volley.Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            Toast.makeText(view.getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                queue.add(stringRequest);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("sepatu", sepatu);
                Fragment fragment = new UpdateSepatu();
                fragment.setArguments(args);

                dismiss();

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment)
                        .setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out) // popExit
                        .addToBackStack(null).commit();
            }
        });

        loadSepatu(sepatu);

        return view;
    }

    private void loadSepatu(Sepatu sepatu) {
        tvId = view.findViewById(R.id.tvIdSepatu);
        tvJenisLayanan = view.findViewById(R.id.tvLayananSepatu);
        tvKondisi = view.findViewById(R.id.tvKondisiSepatu);
        tvJenisSepatu = view.findViewById(R.id.tvJenisSepatu);

        tvId.setText(String.valueOf(sepatu.getStringId()));
        tvJenisLayanan.setText(sepatu.getJenis_layanan());
        tvKondisi.setText(sepatu.getKondisi());
        tvJenisSepatu.setText(sepatu.getJenis_sepatu());
    }
}