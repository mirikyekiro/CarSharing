package com.example.myapplication.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DataBase;

public class FragmentRegistration3 extends Fragment {

    public static final String APP_PREFERENCES = "myProfile";
    public static final String APP_PREFERENCES_LOGIN= "Login";
    public static final String APP_PREFERENCES_MAIL = "Mail";
    public static final String APP_PREFERENCES_PHONE = "Phone";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    public static final String APP_PREFERENCES_IS_RENT = "IsRent";
    public static final String APP_PREFERENCES_ID_RENT_CAR = "IDRentCar";
    public static final String DATA_SAVE = "DATA_SAVE";
    SharedPreferences sPref;
    EditText editPhone, editPassword;
    Button btnLogin, btnToRegistration;

    public static FragmentRegistration3 newInstance(String param1, String param2) {
        FragmentRegistration3 fragment = new FragmentRegistration3();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration3, container, false);

        Activity activity = getActivity();
        DataBase db = new DataBase(activity);

        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        editPhone = view.findViewById(R.id.loginPhone);
        editPassword = view.findViewById(R.id.loginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnToRegistration = view.findViewById(R.id.btnToRegistr);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.FindUser(editPhone.getText().toString(), editPassword.getText().toString()))
                {
                    ed.putBoolean(DATA_SAVE, true);
                    ed.putString(APP_PREFERENCES_LOGIN, db.readDataUser("Login", editPhone.getText().toString()));
                    ed.putString(APP_PREFERENCES_MAIL, db.readDataUser("Mail", editPhone.getText().toString()));
                    ed.putString(APP_PREFERENCES_PHONE, db.readDataUser("Phone", editPhone.getText().toString()));
                    ed.putString(APP_PREFERENCES_PASSWORD, db.readDataUser("Password", editPhone.getText().toString()));
                    ed.putString(APP_PREFERENCES_IS_RENT, "false");
                    ed.putString(APP_PREFERENCES_ID_RENT_CAR, "0");
                    ed.commit();

                    activity.startActivityForResult(new Intent(activity, MainActivity.class), 1);
                    activity.finish();
                }
                else
                    Toast.makeText(activity, "Пользователь не найден!",Toast.LENGTH_LONG).show();
            }
        });

        btnToRegistration.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_fragmentRegistration3_to_fragmentRegistration2,
                        null));

        return view;
    }

    public void ClickEvent(View view) {
        Navigation.createNavigateOnClickListener(R.id.action_fragmentRegistration2_to_fragmentRegistration3, null);
    }
}