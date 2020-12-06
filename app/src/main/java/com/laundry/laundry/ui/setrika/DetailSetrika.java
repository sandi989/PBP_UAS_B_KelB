package com.laundry.laundry.ui.setrika;

import android.app.AlertDialog;

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
import com.laundry.laundry.api.SetrikaAPI;
import com.laundry.laundry.model.Setrika;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.POST;

public class DetailSetrika extends DialogFragment {
    private TextView tvId, tvJumlah, tvBerat, tvJenis;
    private ImageButton ibClose;
    private MaterialButton btnDelete, btnUpdate;
    private View view;
    private Setrika setrika;

    public static DetailSetrika newInstance(){ return new DetailSetrika(); }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_setrika, container, false);

        setrika = (Setrika) getArguments().getSerializable("setrika");

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

                                StringRequest stringRequest = new StringRequest(POST, SetrikaAPI.URL_DELETE + setrika.getStringId(), new com.android.volley.Response.Listener<String>() {
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
                        })
                        .show();
            }
        });

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("setrika", setrika);
                Fragment fragment = new UpdateSetrika();
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

        loadSetrika(setrika);

        return view;
    }

    public void loadSetrika(Setrika setrika){
        tvId = view.findViewById(R.id.tvIdSetrika);
        tvJumlah = view.findViewById(R.id.tvJumlahSetrika);
        tvBerat = view.findViewById(R.id.tvBeratSetrika);
        tvJenis = view.findViewById(R.id.tvJenisSetrika);

        tvId.setText(setrika.getStringId());
        tvJumlah.setText(String.valueOf(setrika.getJumlah_pakaian()));
        tvBerat.setText(String.valueOf(setrika.getBerat()));
        tvJenis.setText(setrika.getJenis_pakaian());
    }
}