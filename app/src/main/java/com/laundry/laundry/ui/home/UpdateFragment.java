package com.laundry.laundry.ui.home;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.laundry.laundry.R;
import com.laundry.laundry.database.DatabaseOrder;
import com.laundry.laundry.model.Order;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateFragment extends Fragment {

    TextInputEditText edtJumlah, edtBerat;
    Button saveBtn, deleteBtn, cancelBtn;
    String tempJumlah, tempBerat, layanan;
    Order order;
    Integer jumlah;
    double berat;
    RadioGroup radioGroup;
    RadioButton radioReg, radioKil;

    String formattedDate;

    public UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_update, container, false);
        order = (Order) getArguments().getSerializable("order");

        edtJumlah = view.findViewById(R.id.edit_jumlah);
        edtBerat = view.findViewById(R.id.edit_berat);

        radioGroup = view.findViewById(R.id.radioGroupUpdate_layanan);

        try {
            if((order.getStringId() != null) && (order.getStringJumlah_pakaian() != null) &&
                    (order.getStringBerat() != null) && (order.getLayanan() != null)) {
                edtJumlah.setText(order.getStringJumlah_pakaian());
                edtBerat.setText(order.getStringBerat());

                radioReg = view.findViewById(R.id.radio_edit_reguler);
                radioKil = view.findViewById(R.id.radio_edit_kilat);

                if (order.getLayanan().equals("Reguler")){
                    radioReg.setChecked(true);
                    layanan = radioReg.getText().toString();
                }else{
                    radioKil.setChecked(true);
                    layanan = radioKil.getText().toString();
                }

            } else {
                edtJumlah.setText("");
                edtBerat.setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_edit_reguler:
                        layanan = radioReg.getText().toString();
                        break;
                    case R.id.radio_edit_kilat:
                        layanan = radioKil.getText().toString();
                        break;
                }

            }
        });

        cancelBtn = view.findViewById(R.id.btnCancel);
        saveBtn = view.findViewById(R.id.btnSave);
        deleteBtn = view.findViewById(R.id.btnDelete);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Code untuk menyimpan hasil order yang telah diedit
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempJumlah = edtJumlah.getText().toString();
                tempBerat = edtBerat.getText().toString();

                Date currentTime = Calendar.getInstance().getTime();
                formattedDate = DateFormat.getDateInstance().format(currentTime);

                if(tempJumlah.isEmpty()){
                    edtJumlah.setError("Please fill Correctly");}
                if(tempBerat.isEmpty()){
                    edtBerat.setError("Please fill Correctly");}
                if(!tempJumlah.isEmpty() && !tempBerat.isEmpty()) {
                    jumlah = Integer.parseInt(tempJumlah);
                    berat = Double.parseDouble(tempBerat);

                    order.setJumlah_pakaian(jumlah);
                    order.setBerat(berat);
                    order.setLayanan(layanan);
                    order.setTanggal_masuk(formattedDate);

                    update(order);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),"Update order failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Code untuk memanggil method delete dan menghapus order terpilih
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete(order);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        //Code untuk kembali ke tampilan daftar order
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
                fragmentTransaction.hide(UpdateFragment.this).commit();
            }
        });
    }

    //Method untuk menyimpan hasil order yang diedit
    private void update(final Order order) {
        class UpdateOrder extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseOrder.getInstance(getActivity().getApplicationContext()).getDatabase()
                        .orderDao()
                        .update(order);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity().getApplicationContext(),"Order updated",Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out // popExit
                        );
                fragmentTransaction.hide(UpdateFragment.this).commit();
            }
        }
        UpdateOrder updateOrder = new UpdateOrder();
        updateOrder.execute();
    }

    //Method untuk menhapus order dari database
    private void delete(final Order order) {
        class DeleteOrder extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseOrder.getInstance(getActivity().getApplicationContext()).getDatabase()
                        .orderDao()
                        .delete(order);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity().getApplicationContext(), "Order deleted", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out // popExit
                        );
                fragmentTransaction.hide(UpdateFragment.this).commit();
            }
        }
        DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.execute();
    }
}

















