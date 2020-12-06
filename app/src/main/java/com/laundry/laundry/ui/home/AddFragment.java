package com.laundry.laundry.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.laundry.laundry.R;
import com.laundry.laundry.database.DatabaseOrder;
import com.laundry.laundry.model.Order;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddFragment extends Fragment {

    TextInputEditText edtJumlah, edtBerat;
    Button cancelBtn, addBtn;
    String layanan="";
    String tempJumlah, tempBerat, formattedDate;
    int jumlah;
    double berat;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add, container, false);

        //Menghubungkan dengan layout
        edtJumlah = view.findViewById(R.id.add_jumlah);
        edtBerat = view.findViewById(R.id.add_berat);
        cancelBtn = view.findViewById(R.id.btnCancel);
        addBtn = view.findViewById(R.id.btnAdd);

        //Condition untuk mendapatkan value dari button layanan yang dipilih
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_layanan);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_reguler:
                        RadioButton radioReg = view.findViewById(R.id.radio_reguler);
                        layanan = radioReg.getText().toString();
                        break;
                    case R.id.radio_kilat:
                        RadioButton radioKil = view.findViewById(R.id.radio_kilat);
                        layanan = radioKil.getText().toString();
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
                fragmentTransaction.hide(AddFragment.this).commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempJumlah = edtJumlah.getText().toString();
                tempBerat = edtBerat.getText().toString();

                //Dapatkan tanggal sesuai sistem
                Date currentTime = Calendar.getInstance().getTime();
                formattedDate = DateFormat.getDateInstance().format(currentTime);

                //Exception jika field/ button null
                if(tempJumlah.isEmpty()){
                    edtJumlah.setError("Silakan diisi dengan benar");}
                if(tempBerat.isEmpty()){
                    edtBerat.setError("Silakan diisi dengan benar");}
                if (layanan.length()==0){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Silakan pilih layanan", Toast.LENGTH_SHORT).show();
                }
                if(!tempJumlah.isEmpty() && !tempBerat.isEmpty() && layanan.length()>0) {
                    jumlah = Integer.parseInt(tempJumlah);
                    berat = Double.parseDouble(tempBerat);
                    addOrder();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out // popExit
                            );
                    fragmentTransaction.hide(AddFragment.this).addToBackStack(null).commit();
                }
            }
        });
    }

    //Method untuk memasukkan order ke dalam database
    private void addOrder(){
        class AddUser extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                Order order = new Order();
                order.setBerat(berat);
                order.setJumlah_pakaian(jumlah);
                order.setLayanan(layanan);
                order.setTanggal_masuk(formattedDate);

                DatabaseOrder.getInstance(getContext())
                        .getDatabase()
                        .orderDao()
                        .insert(order);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity().getApplicationContext(), "Order saved", Toast.LENGTH_SHORT).show();
            }
        }
        AddUser add = new AddUser();
        add.execute();
    }
}