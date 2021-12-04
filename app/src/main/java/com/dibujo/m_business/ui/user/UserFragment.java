package com.dibujo.m_business.ui.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.User;
import com.dibujo.m_business.UserAdapter;
import com.dibujo.m_business.databinding.FragmentUserBinding;

import java.util.ArrayList;
import java.util.Collections;

public class UserFragment extends Fragment {

    private UserViewModel homeViewModel;
    private FragmentUserBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    private Spinner sp;
    EditText nameET, codeET;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<User> listUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.addUserButton.setOnClickListener(view -> {
            createAddDialog();
        });


        sp = binding.spinnerOrder;

        String[] optionsOrder = {"Nombre A-Z","Nombre Z-A", "Código"};
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsOrder);
        sp.setAdapter(optionsAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

        });


        /*final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        recyclerView = binding.userRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listUser = new ArrayList<>();

        //Delete this code
        //for (int i = 0; i< 3; ++i){
           // listUser.add(new User("Juan" + i + "Chorrillos", "U-00"+i));
       // }
        listUser.add(new User("Juan", "U-00"+1));
        listUser.add(new User("Luis", "U-00"+2));
        listUser.add(new User("Alberto", "U-00"+3));
        listUser.add(new User("Maria", "U-00"+4));
        //end delete

        //Firebase code here
        //................
        // end firebase

        userAdapter = new UserAdapter(getContext(), listUser);
        recyclerView.setAdapter(userAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createAddDialog(){
        aBuilder = new AlertDialog.Builder(getContext());
        View addUserPopup = getLayoutInflater().inflate(R.layout.add_user, null);
        aBuilder.setView(addUserPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        addUserPopup.findViewById(R.id.addUser_save_button).setOnClickListener(view -> {
            codeET = addUserPopup.findViewById(R.id.addUser_code);
            nameET = addUserPopup.findViewById(R.id.addUser_name);
            listUser.add(new User(nameET.getText().toString(), codeET.getText().toString()));
            order(sp.getSelectedItemPosition());
            //Firebase save here
            //..........
            //end save
            dialog.dismiss();
        });
        addUserPopup.findViewById(R.id.addUser_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i){
        switch (i){
            case 0:
                Collections.sort(listUser, User.userNameAZComparator);
                break;
            case 1:
                Collections.sort(listUser, User.userNameZAComparator);
                break;
            case 2:
                Collections.sort(listUser, User.userCodeComparator);
                break;

        }
        userAdapter.notifyDataSetChanged();
    }
}