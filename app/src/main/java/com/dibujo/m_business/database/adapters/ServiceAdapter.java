package com.dibujo.m_business.database.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.database.Service;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    Context contex;
    ArrayList<Service> list;

    public ServiceAdapter(Context contex, ArrayList<Service> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public ServiceAdapter.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_service, parent, false);
        return new ServiceAdapter.ServiceViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ServiceViewHolder holder, int position) {
        Service s = list.get(position);
        holder.statusT.setText(s.getStatus());
        holder.nameT.setText(s.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, statusT;
        CardView cardView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.service_name);
            statusT = itemView.findViewById(R.id.service_status);
            cardView = itemView.findViewById(R.id.cardview_service);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 101, 0, "Editar");
            if(statusT.getText().equals("Activo")){
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Inactivar");
            }else{
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Activar");
            }

            contextMenu.add(this.getAdapterPosition(), 103, 0, "Eliminar");
            MenuItem item = contextMenu.getItem(2);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
    }

}

