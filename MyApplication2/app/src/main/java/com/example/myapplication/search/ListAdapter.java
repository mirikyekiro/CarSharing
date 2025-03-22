package com.example.myapplication.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CarList;
import com.example.myapplication.MainActivity;
import com.example.myapplication.OnItemClickListener;
import com.example.myapplication.R;
import com.example.myapplication.fragments.CardOfCar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    List<CarList> carList = new ArrayList<>();
    List<CarList> carListAll = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    public ListAdapter(Activity activity,
                       Context context,
                       ArrayList<CarList> carList,
                       OnItemClickListener onItemClickListener){
        this.activity = activity;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.carList = carList;
        this.carListAll = new ArrayList<>(carList);
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, final int position) {

        CarList cList = carList.get(position);

        holder.auto_name_text.setText(String.valueOf(cList.getAuto_name()));
        holder.auto_number_text.setText(String.valueOf(cList.getAuto_number()));
        if(String.valueOf(cList.getAuto_status()).contains("1"))
        {
            holder.auto_status_text.setText("Свободно");
            holder.auto_status_text.setTextColor(Color.parseColor("#008000"));
        }
        else {
            holder.auto_status_text.setText("Занято");
            holder.auto_status_text.setTextColor(Color.parseColor("#ff0000"));
        }
        holder.auto_gaz_text.setText("Уровень топлива: "+ String.valueOf(cList.getAuto_gaz()) + " %");
        holder.auto_price_text.setText("от "+String.valueOf(cList.getAuto_price()) + " руб/мин");

        try
        {
            InputStream ims = context.getAssets().open(String.valueOf(cList.getAuto_image()));
            Drawable d = Drawable.createFromStream(ims, null);
            holder.auto_image_imageView.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
            return;
        }


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("auto_id", String.valueOf(cList.getAuto_id()));
                bundle.putString("auto_name", String.valueOf(cList.getAuto_name()));
                bundle.putString("auto_number", String.valueOf(cList.getAuto_number()));
                bundle.putString("auto_gaz", String.valueOf(cList.getAuto_gaz()));
                bundle.putString("auto_status", String.valueOf(cList.getAuto_status()));
                bundle.putString("auto_price", String.valueOf(cList.getAuto_price()));
                bundle.putString("auto_imageBIG", String.valueOf(cList.getAuto_image()));

                ((MainActivity) activity).tabLayout.getTabAt(0).select();
                ((MainActivity) activity).ZoomToMarker(String.valueOf(cList.getAuto_id()));
                ((MainActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.FCV_tab, CardOfCar.newInstance(bundle)).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void setCarList(ArrayList<CarList> carList) {
        this.carListAll = carList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView auto_name_text, auto_number_text,
                auto_gaz_text, auto_status_text, auto_price_text;
        ImageView auto_image_imageView;
        CardView mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            auto_name_text = itemView.findViewById(R.id.listName);
            auto_number_text = itemView.findViewById(R.id.listModel);
            auto_gaz_text = itemView.findViewById(R.id.listGaz);
            auto_status_text = itemView.findViewById(R.id.listStatus);
            auto_price_text = itemView.findViewById(R.id.listPrice);
            auto_image_imageView = itemView.findViewById(R.id.image_itemList);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    public Filter getFilter()
    {
        return filter;
    }

    Filter filter = new Filter()
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CarList> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty())
                filteredList.addAll(carListAll);
            else {
                for(CarList item : carListAll){
                    if(item.getAuto_name().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(item);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            carList.clear();
            carList.addAll((Collection<? extends CarList>) results.values);
            notifyDataSetChanged();
        }
    };
}
