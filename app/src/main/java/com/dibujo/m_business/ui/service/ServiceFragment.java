package com.dibujo.m_business.ui.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.database.Service;
import com.dibujo.m_business.database.Service;
import com.dibujo.m_business.database.adapters.ServiceAdapter;
import com.dibujo.m_business.databinding.FragmentServiceBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceFragment extends Fragment {

    private ServiceViewModel serviceViewModel;
    private FragmentServiceBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    private Spinner sp;
    EditText nameET;
    CheckBox statusCB;

    FirebaseDatabase db;
    RecyclerView recyclerView;
    ServiceAdapter serviceAdapter;
    ArrayList<Service> listService;
    String idCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serviceViewModel =
                new ViewModelProvider(this).get(ServiceViewModel.class);

        binding = FragmentServiceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseDatabase.getInstance();

        binding.addServiceButton.setOnClickListener(view -> {
            createAddDialog();
        });


        sp = binding.spinnerOrder;

        String[] optionsOrder = {"Nombre A-Z","Nombre Z-A", "Estado"};
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

        recyclerView = binding.serviceRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listService = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(getContext(), listService);
        recyclerView.setAdapter(serviceAdapter);

        //Firebase code here
        db.getReference("servicio").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listService.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Service s = ds.getValue(Service.class);
                    if(s.getStatus().equals("*"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listService.add(s);
                }
                order(sp.getSelectedItemPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });
        //id
        db.getReference("idCont").child("servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idCount = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error+"");
            }
        });
        // end firebase

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createAddDialog(){
        aBuilder = new AlertDialog.Builder(getContext());
        View addServicePopup = getLayoutInflater().inflate(R.layout.add_service, null);
        aBuilder.setView(addServicePopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        addServicePopup.findViewById(R.id.addService_save_button).setOnClickListener(view -> {
            nameET = addServicePopup.findViewById(R.id.addService_name);
            statusCB = addServicePopup.findViewById(R.id.addService_status);
            Service nS = new Service();
            nS.setName(nameET.getText().toString());
            if(statusCB.isChecked()){
                nS.setStatus("Activo");
            }else{
                nS.setStatus("Inactivo");
            }
            listService.add(nS);
            //Firebase save here
            add(nS);
            //end save
            dialog.dismiss();
        });
        addServicePopup.findViewById(R.id.addService_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
                Collections.sort(listService, Service.serviceNameAZComparator);
                break;
            case 1:
                Collections.sort(listService, Service.serviceNameZAComparator);
                break;
            case 2:
                Collections.sort(listService, Service.serviceStatusComparator);
                break;

        }
        serviceAdapter.notifyDataSetChanged();
    }
    public void createEditDialog(Service s){
        aBuilder = new AlertDialog.Builder(getContext());
        View addServicePopup = getLayoutInflater().inflate(R.layout.add_service, null);
        aBuilder.setView(addServicePopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        nameET = addServicePopup.findViewById(R.id.addService_name);
        statusCB = addServicePopup.findViewById(R.id.addService_status);
        addServicePopup.findViewById(R.id.addService_save_button).setOnClickListener(view -> {
            s.setName(nameET.getText().toString());
            if(statusCB.isChecked())    s.setStatus("Activo");
            else    s.setStatus("Inactivo");
            //Firebase edit here
            update(s);
            //end edit save
            dialog.dismiss();
        });
        addServicePopup.findViewById(R.id.addService_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });
        //Set values of database into edit form
        nameET.setText(s.getName());
        statusCB.setChecked(s.getStatus().equals("Activo"));

    }

    public void update(Service s){
        db.getReference("servicio").child(s.getId()).updateChildren(s.toMap());
    }

    public void add(Service s){
        s.setId("S"+generateId());
        //db.getReference("servicio").push().setValue(s.toMap());
        db.getReference("servicio").child(s.getId()).setValue(s.toMap());
    }

    public void delete(Service s){
        db.getReference("servicio").child(s.getId()).updateChildren(s.toMap());
        //This is a physical remove
        //db.getReference("servicio").child(s.getId()).removeValue();
    }

    public String generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        String idS = "000"+idInt;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("servicio", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idS.substring(idS.length()-3);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        Service s = listService.get(item.getGroupId());
        switch (item.getItemId()){
            case 101:
                createEditDialog(s);
                serviceAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 102:
                if(s.getStatus().equals("Activo")) s.setStatus("Inactivo");
                else    s.setStatus("Activo");
                update(s);
                serviceAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 103:
                s.setStatus("*");
                listService.remove(s);
                delete(s);
                serviceAdapter.notifyItemRemoved(item.getGroupId());
                break;
        }
        order(sp.getSelectedItemPosition());
        return true;
    }
}