package com.example.myapplication.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.database.DataBase;
import com.example.myapplication.registration.Registration;

public class ProfileFragment extends Fragment {

    public static final String APP_PREFERENCES = "myProfile";
    public static final String APP_PREFERENCES_LOGIN = "Login";
    public static final String APP_PREFERENCES_MAIL = "Mail";
    public static final String APP_PREFERENCES_PHONE = "Phone";
    public static final String APP_PREFERENCES_IS_RENT = "IsRent";

    SharedPreferences sPref;

    TextView textLogin, textMail, textPhone;
    Button btnDeleteProfile;
    ImageButton btnLogOut;
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        Activity activity = getActivity();

        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        textLogin = view.findViewById(R.id.text_login);
        textMail = view.findViewById(R.id.text_mail);
        textPhone = view.findViewById(R.id.text_phone);

        btnDeleteProfile = view.findViewById(R.id.btn_deleteProfile);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        textLogin.setText(sPref.getString(APP_PREFERENCES_LOGIN, "NULL"));
        textMail.setText(sPref.getString(APP_PREFERENCES_MAIL, "NULL"));
        textPhone.setText(sPref.getString(APP_PREFERENCES_PHONE, "NULL"));

        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sPref.getString(APP_PREFERENCES_IS_RENT, "NULL").equals("false"))
                    showInfoAlert(1,"Вы точно хотите удалить профиль?",
                        sPref.getString(APP_PREFERENCES_PHONE, "NULL"));
                else
                    Toast.makeText(view.getContext(), "Завершите поездку!",Toast.LENGTH_LONG).show();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sPref.getString(APP_PREFERENCES_IS_RENT, "NULL").equals("false"))
                    showInfoAlert(2,"Вы точно хотите выйти из аккаунта?",
                        sPref.getString(APP_PREFERENCES_PHONE, "NULL"));
                else
                    Toast.makeText(view.getContext(), "Завершите поездку!",Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void showInfoAlert(int type, String text, String phone){
        Activity activity = getActivity();
        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(type == 1)
        {
            builder.setTitle("Удаление профиля")
                    .setMessage(text)
                    .setCancelable(false)
                    .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ed.clear();
                            ed.commit();

                            DataBase myDB = new DataBase(getActivity());
                            myDB.deleteUser(phone);

                            startActivity(new Intent(activity, Registration.class));
                            activity.finish();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }

        else {
            builder.setTitle("Выйти из аккаунта")
                    .setMessage(text)
                    .setCancelable(false)
                    .setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ed.clear();
                            ed.commit();

                            startActivity(new Intent(activity, Registration.class));
                            activity.finish();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}