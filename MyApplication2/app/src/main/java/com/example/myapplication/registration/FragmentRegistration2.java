package com.example.myapplication.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.DataBase;
import com.google.android.material.textfield.TextInputLayout;

public class FragmentRegistration2 extends Fragment {
    public static final String APP_PREFERENCES = "myProfile";
    public static final String APP_PREFERENCES_LOGIN= "Login";
    public static final String APP_PREFERENCES_MAIL = "Mail";
    public static final String APP_PREFERENCES_PHONE = "Phone";
    public static final String APP_PREFERENCES_PASSWORD = "Password";
    public static final String DATA_SAVE = "DATA_SAVE";
    Button btnSave, btnToLogIn;;
    EditText editTextLogin, editTextMail, editTextPhone, editTextPassword;
    SharedPreferences sPref;

    DataBase db;
    TextInputLayout loginLayout, mailLayout, phoneLayout, passwordLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration2, container, false);

        Activity activity = getActivity();
        db = new DataBase(activity);

        editTextLogin = view.findViewById(R.id.editLogin);
        editTextMail = view.findViewById(R.id.editMail);
        editTextPhone = view.findViewById(R.id.editPhone);
        editTextPassword = view.findViewById(R.id.editPassword);

        EditText[] edList = {editTextLogin, editTextMail, editTextPhone, editTextPassword};

        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnToLogIn = view.findViewById(R.id.btnToLogin);

        loginLayout = view.findViewById(R.id.loginError);
        mailLayout = view.findViewById(R.id.mailError);
        phoneLayout = view.findViewById(R.id.phoneError);
        passwordLayout = view.findViewById(R.id.passwordError);

        btnSave.setEnabled(false);

        CustomTextWatcher textWatcher = new CustomTextWatcher(edList, btnSave);
        for (EditText editText : edList) editText.addTextChangedListener(textWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMail = editTextMail.getText().toString();

                String strPhone = editTextPhone.getText().toString();

                String strPassword = editTextPassword.getText().toString();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(strMail).matches())
                    mailLayout.setError("Проверьте правильность написания почты");
                else if (strPhone.length() != 11) {
                    mailLayout.setError(null);
                    phoneLayout.setError("Проверьте правильность написания номера телефона");
                }
                else if (strPassword.length() < 10) {
                    phoneLayout.setError(null);
                    passwordLayout.setError("Пароль слишком короткий");
                }
                else {
                    passwordLayout.setError(null);
                    closeKeyboard();
                    Toast.makeText(activity, "Сохранение завершено!",Toast.LENGTH_LONG).show();

                    db.addUser(editTextLogin.getText().toString(),
                            strMail,
                            strPhone,
                            strPassword);
                    SaveData();

                    activity.startActivityForResult(new Intent(activity, MainActivity.class), 1);
                    activity.finish();
                }
            }
        });

        btnToLogIn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentRegistration2_to_fragmentRegistration3, null));

        return view;
    }

    private void SaveData(){
        Activity activity = getActivity();

        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        ed.putBoolean(DATA_SAVE, true);
        ed.putString(APP_PREFERENCES_LOGIN, editTextLogin.getText().toString());
        ed.putString(APP_PREFERENCES_MAIL, editTextMail.getText().toString());
        ed.putString(APP_PREFERENCES_PHONE, editTextPhone.getText().toString());
        ed.putString(APP_PREFERENCES_PASSWORD, editTextPassword.getText().toString());
        ed.commit();
    }

    private void closeKeyboard(){
        View view = this.getActivity().getCurrentFocus();
        if(view !=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

class CustomTextWatcher implements TextWatcher {
    View v;
    EditText[] edList;
    public CustomTextWatcher(EditText[] edList, Button v) {
        this.v = v;
        this.edList = edList;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        for (EditText editText : edList) {
            if (editText.getText().toString().trim().length() <= 0) {
                v.setEnabled(false);
                v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9C8F4E")));
                break;
            } else {
                v.setEnabled(true);
                v.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEFCD25")));
            }
        }
    }
}