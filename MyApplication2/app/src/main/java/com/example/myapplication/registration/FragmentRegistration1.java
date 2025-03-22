package com.example.myapplication.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

public class FragmentRegistration1 extends Fragment {

    Button btn, btn2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration1, container, false);

        btn = view.findViewById(R.id.btnCreateProfile);
        btn2 = view.findViewById(R.id.btnLogIn);
        btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentRegistration1_to_fragmentRegistration2, null));
        btn2.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_fragmentRegistration1_to_fragmentRegistration3, null));

        return view;
    }
}