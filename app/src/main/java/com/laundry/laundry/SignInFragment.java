package com.laundry.laundry;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private String CHANNEL_ID = "Channel 1";
    private FirebaseAuth.AuthStateListener authStateListener;
    Button signInBtn;
    EditText edtEmail, edtPassword;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);

        edtEmail = root.findViewById(R.id.signIn_email);
        edtPassword = root.findViewById(R.id.signIn_password);
        firebaseAuth = FirebaseAuth.getInstance();

        signInBtn = root.findViewById(R.id.btnSignIn);

        progressBar = root.findViewById(R.id.signInProgressBar);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            com.google.firebase.auth.FirebaseUser FirebaseUser = firebaseAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseUser != null){
                    Toast.makeText(getActivity().getApplicationContext(),"Login Successfull",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent (getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().equalsIgnoreCase("") || !isValidEmailId(edtEmail.getText().toString().trim())){
                    Toast.makeText(getActivity().getApplicationContext(),"Email Invalid",Toast.LENGTH_SHORT).show();
                }else if(edtPassword.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity().getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
                }else if(edtPassword.getText().toString().length()<6){
                    Toast.makeText(getActivity().getApplicationContext(), "Password too short", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), "SignIn Failed", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }else{
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    Toast.makeText(getActivity().getApplicationContext(), "Sign In Successfull", Toast.LENGTH_SHORT).show();
                                    createNotificationChannel();
                                    addNotification();
                                    Intent i = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    Toast.makeText(getActivity().getApplicationContext(), "Please Verification Your Email", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
        return root;
    }

    //Melakukan build notifikasi
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Channel 1";
            String description = "This is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Membuat notifikasi saat pengguna masuk ke aplikasi
    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Hello >_<")
                .setContentText("Welcome Back, Please Enjoy Your Stay...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity().getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    //Validasi pattern email
    private boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

}