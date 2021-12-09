package com.dibujo.m_business.database.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.dibujo.m_business.database.BackupList;
import com.dibujo.m_business.database.DocumentType;
import com.dibujo.m_business.database.Form;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.FormViewHolder> {

    Context contex;
    ArrayList<Form> list;
    FirebaseDatabase db;


    public FormAdapter(Context contex, ArrayList<Form> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public FormAdapter.FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_form, parent, false);
        db = FirebaseDatabase.getInstance();
        return new FormAdapter.FormViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull FormAdapter.FormViewHolder holder, int position) {
        Form s = list.get(position);
        holder.dateT.setText(s.getEnrollmentDate());
        holder.nameT.setText(s.getName());

        db.getReference("servicio").child(s.getService()).child("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                s.setServiceName(snapshot.getValue(String.class));
                holder.serviceT.setText(s.getServiceName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        db.getReference("empresa").child(s.getCompany()).child("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                s.setCompanyName(snapshot.getValue(String.class));
                holder.companyT.setText(s.getCompanyName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        holder.statusTS = s.getStatus();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FormViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, dateT, companyT, serviceT;
        String statusTS;
        CardView cardView;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.form_name);
            dateT = itemView.findViewById(R.id.form_date);
            companyT = itemView.findViewById(R.id.form_company);
            serviceT = itemView.findViewById(R.id.form_service);
            cardView = itemView.findViewById(R.id.cardview_form);
            cardView.setOnCreateContextMenuListener(this);

        }

        @SuppressLint("ResourceType")
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 100, 0, "Ver");
            contextMenu.add(this.getAdapterPosition(), 101, 0, "Editar");
            if(statusTS.equals("Activo")){
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Inactivar");
            }else{
                contextMenu.add(this.getAdapterPosition(), 102, 0, "Activar");
            }

            contextMenu.add(this.getAdapterPosition(), 103, 0, "Eliminar");
            MenuItem item = contextMenu.getItem(3);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filtered(String txt){
        int len = txt.length();
        if(len == 0){
            list.clear();
            list.addAll(BackupList.listFormBackup);
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Form> coll = list.stream()
                        .filter(i -> i.getName().toLowerCase().contains(txt.toLowerCase()))
                        .collect(Collectors.toList());
                list.clear();
                list.addAll(coll);
            }else{
                list.clear();
                for (Form dt: BackupList.listFormBackup) {
                    if(dt.getName().toLowerCase().contains(txt.toLowerCase())){
                        list.add(dt);
                    }
                }
            }
        }
        notifyDataSetChanged();

    }

}