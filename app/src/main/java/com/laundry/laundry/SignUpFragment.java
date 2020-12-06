package com.laundry.laundry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    Button signUpBtn;
    TextInputEditText edtNama, edtEmail, edtPassword, edtAlamat;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    DatabaseReference reference;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signUpBtn = root.findViewById(R.id.btnSignUp);

        edtNama = root.findViewById(R.id.signUp_nama);
        edtAlamat = root.findViewById(R.id.signUp_alamat);
        edtEmail = root.findViewById(R.id.signUp_email);
        edtPassword = root.findViewById(R.id.signUp_password);

        progressBar = root.findViewById(R.id.signUpProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nama_input = edtNama.getText().toString();
                final String alamat_input = edtAlamat.getText().toString();
                final String email_input = edtEmail.getText().toString();
                final String password_input = edtPassword.getText().toString();

                if(edtEmail.getText().toString().equalsIgnoreCase("") || !isValidEmailId(edtEmail.getText().toString().trim())){
                    Toast.makeText(getActivity().getApplicationContext(),"Email Invalid",Toast.LENGTH_SHORT).show();
                }else if(edtPassword.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity().getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
                }else if(edtPassword.getText().toString().length()<6){
                    Toast.makeText(getActivity().getApplicationContext(), "Password too short", Toast.LENGTH_SHORT).show();
                }else{
                    register(nama_input,alamat_input,email_input,password_input);
                }
            }
        });
        return root;
    }

    private void register(String nama_input, String alamat_input, String email_input, String password_input) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email_input,password_input).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Authentication Successful", Toast.LENGTH_SHORT).show();

//                  Memasukkan Data user ke dalam Database Firebase
                    FirebaseUser rUser = mFirebaseAuth.getCurrentUser();

                    assert rUser != null;
                    String userId = rUser.getUid();

                    reference =  FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String,String> hashMap = new HashMap<>();

                    hashMap.put("userId",userId);
                    hashMap.put("nama",nama_input);
                    hashMap.put("alamat",alamat_input);
                    hashMap.put("email",email_input);
                    hashMap.put("password",computedMD5Hash(edtPassword.getText().toString()).toString());

                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity().getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                progressBar.setVisibility(View.GONE);

                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(
                                                R.anim.slide_in,  // enter
                                                R.anim.fade_out,  // exit
                                                R.anim.fade_in,   // popEnter
                                                R.anim.slide_out // popExit
                                        );
                                fragmentTransaction.replace(R.id.signUp_layout, new SignInFragment()).addToBackStack(null).commit();
                            }
                            else {
                                Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //Validasi pattern dari email
    private boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public StringBuffer computedMD5Hash(String password){
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDiggest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for(int i=0; i< messageDiggest.length;i++){
                String h = Integer.toHexString(0xFF & messageDiggest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            return MD5Hash;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }
}