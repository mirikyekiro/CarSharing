package com.example.myapplication.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.CarList;
import com.example.myapplication.OnItemClickListener;
import com.example.myapplication.R;
import com.example.myapplication.database.DataBase;
import com.example.myapplication.search.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchFragment extends Fragment{
    RecyclerView recyclerView;
    DataBase db;
    ListAdapter listAdapter;
    ArrayList<CarList> carList = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    SearchView searchView;
    ImageButton btnSwap, btnSync;
    Spinner spinnerSort;
    boolean isSwipe;
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Activity activity = getActivity();
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        btnSwap = view.findViewById(R.id.btnSwap);
        btnSync = view.findViewById(R.id.btnSync);
        spinnerSort = view.findViewById(R.id.spinner);

        db = new DataBase(activity);

        StoreDataInArrays();

        listAdapter = new ListAdapter(activity, getContext(), carList, onItemClickListener);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });

        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
                if(isSwipe) {
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    isSwipe = false;
                }
                else
                {
                    linearLayoutManager.setReverseLayout(false);
                    linearLayoutManager.setStackFromEnd(false);
                    isSwipe = true;
                }
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.notifyDataSetChanged();
            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortData(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    private void sortData(int i) {

        Collections.sort(carList, new Comparator<CarList>() {
            @Override
            public int compare(CarList o1, CarList o2) {
                int sort = 0;
                switch (i)
                {
                    case 0 : sort = o1.getAuto_name().compareToIgnoreCase(o2.getAuto_name()); break;
                    case 1 : sort =  o1.getAuto_status().compareToIgnoreCase(o2.getAuto_status()); break;
                    case 2 : sort =  o1.getAuto_gaz().compareToIgnoreCase(o2.getAuto_gaz()); break;
                    case 3 : sort =  o1.getAuto_price().compareToIgnoreCase(o2.getAuto_price()); break;
                }

                return sort;
            }
        });
        listAdapter.setCarList(carList);
    }

    private void StoreDataInArrays() {
        Cursor cursor = db.readAllData(false, "0");
        if(cursor.getCount() == 0){
            Toast.makeText(getActivity(), "Нет данных", Toast.LENGTH_LONG).show();

        }
        else{
            while(cursor.moveToNext()){
                carList.add(new CarList(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(7),
                        cursor.getFloat(8),
                        cursor.getFloat(9)));
            }
        }
    }
}