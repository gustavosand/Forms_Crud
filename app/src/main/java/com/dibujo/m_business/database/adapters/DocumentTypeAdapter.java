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
import com.dibujo.m_business.User;
import com.dibujo.m_business.database.DocumentType;

import java.util.ArrayList;

public class DocumentTypeAdapter extends RecyclerView.Adapter<DocumentTypeAdapter.DocumentTypeViewHolder> {

    Context contex;
    ArrayList<DocumentType> list;

    public DocumentTypeAdapter(Context contex, ArrayList<DocumentType> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public DocumentTypeAdapter.DocumentTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_document, parent, false);
        return new DocumentTypeAdapter.DocumentTypeViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull DocumentTypeAdapter.DocumentTypeViewHolder holder, int position) {
        DocumentType dt = list.get(position);
        holder.statusT.setText(dt.getStatus());
        holder.nameT.setText(dt.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DocumentTypeViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameT, statusT;
        CardView cardView;

        public DocumentTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.documentType_name);
            statusT = itemView.findViewById(R.id.documentType_status);
            cardView = itemView.findViewById(R.id.cardview_document);
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
