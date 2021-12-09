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
import com.dibujo.m_business.database.Company;

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    Context contex;
    ArrayList<Company> list;

    public CompanyAdapter(Context contex, ArrayList<Company> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public CompanyAdapter.CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_company, parent, false);
        return new CompanyAdapter.CompanyViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.CompanyViewHolder holder, int position) {
        Company s = list.get(position);
        holder.codeT.setText(s.getId());
        holder.nameT.setText(s.getName());
        holder.rucT.setText(s.getRuc());
        holder.statusTS = s.getStatus();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, codeT, rucT;
        String statusTS;
        CardView cardView;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.company_name);
            rucT = itemView.findViewById(R.id.company_ruc);
            codeT = itemView.findViewById(R.id.company_code);
            cardView = itemView.findViewById(R.id.cardview_company);
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

}

