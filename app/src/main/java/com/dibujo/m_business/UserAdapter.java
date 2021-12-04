package com.dibujo.m_business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context contex;
    ArrayList<User> list;

    public UserAdapter(Context contex, ArrayList<User> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contex).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = list.get(position);
        holder.codT.setText(user.getCode());
        holder.nameT.setText(user.getName());
        holder.deleteUserB.setOnClickListener(view -> {
            list.remove(user);
            notifyDataSetChanged();
        });

        holder.editUserB.setOnClickListener(view -> {
            editUser(user);

            notifyDataSetChanged();
        });

    }

    private void editUser(User user) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameT, codT;
        Button deleteUserB, editUserB;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            nameT = itemView.findViewById(R.id.user_name);
            codT = itemView.findViewById(R.id.user_code);
            deleteUserB = itemView.findViewById(R.id.deleteUser_button);
            editUserB = itemView.findViewById(R.id.editUser_button);

        }
    }
}
