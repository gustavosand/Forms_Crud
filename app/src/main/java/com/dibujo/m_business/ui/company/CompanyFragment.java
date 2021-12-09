package com.dibujo.m_business.ui.company;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.database.Company;
import com.dibujo.m_business.database.adapters.CompanyAdapter;
import com.dibujo.m_business.databinding.FragmentCompanyBinding;
import com.dibujo.m_business.databinding.FragmentCompanyBinding;
import com.dibujo.m_business.ui.company.CompanyViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CompanyFragment extends Fragment {

    private CompanyViewModel companyViewModel;
    private FragmentCompanyBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    private Spinner sp;
    EditText nameET;
    EditText addressET;
    EditText rucET;
    CheckBox statusCB;

    TextView nameTV, addressTV, statusTV, rucTV;

    FirebaseDatabase db;
    RecyclerView recyclerView;
    CompanyAdapter companyAdapter;
    ArrayList<Company> listCompany;
    String idCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        companyViewModel =
                new ViewModelProvider(this).get(CompanyViewModel.class);

        binding = FragmentCompanyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseDatabase.getInstance();

        binding.addCompanyButton.setOnClickListener(view -> {
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

        recyclerView = binding.companyRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listCompany = new ArrayList<>();
        companyAdapter = new CompanyAdapter(getContext(), listCompany);
        recyclerView.setAdapter(companyAdapter);

        //Firebase code here
        db.getReference("empresa").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCompany.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Company s = ds.getValue(Company.class);
                    if(s.getStatus().equals("*"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listCompany.add(s);
                }
                order(sp.getSelectedItemPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });
        //id
        db.getReference("idCont").child("empresa").addValueEventListener(new ValueEventListener() {
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
        View addCompanyPopup = getLayoutInflater().inflate(R.layout.add_company, null);
        aBuilder.setView(addCompanyPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        addCompanyPopup.findViewById(R.id.addCompany_save_button).setOnClickListener(view -> {
            nameET = addCompanyPopup.findViewById(R.id.addCompany_name);
            statusCB = addCompanyPopup.findViewById(R.id.addCompany_status);
            rucET = addCompanyPopup.findViewById(R.id.addCompany_ruc);
            addressET = addCompanyPopup.findViewById(R.id.addCompany_address);
            Company nS = new Company();
            nS.setName(nameET.getText().toString());
            if(statusCB.isChecked()){
                nS.setStatus("Activo");
            }else{
                nS.setStatus("Inactivo");
            }
            nS.setRuc(rucET.getText().toString());
            nS.setAddress(addressET.getText().toString());
            listCompany.add(nS);
            //Firebase save here
            add(nS);
            //end save
            dialog.dismiss();
        });
        addCompanyPopup.findViewById(R.id.addCompany_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
                Collections.sort(listCompany, Company.companyNameAZComparator);
                break;
            case 1:
                Collections.sort(listCompany, Company.companyNameZAComparator);
                break;
            case 2:
                Collections.sort(listCompany, Company.companyStatusComparator);
                break;

        }
        companyAdapter.notifyDataSetChanged();
    }
    public void createEditDialog(Company s){
        aBuilder = new AlertDialog.Builder(getContext());
        View addCompanyPopup = getLayoutInflater().inflate(R.layout.add_company, null);
        aBuilder.setView(addCompanyPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        nameET = addCompanyPopup.findViewById(R.id.addCompany_name);
        statusCB = addCompanyPopup.findViewById(R.id.addCompany_status);
        rucET = addCompanyPopup.findViewById(R.id.addCompany_ruc);
        addressET = addCompanyPopup.findViewById(R.id.addCompany_address);
        addCompanyPopup.findViewById(R.id.addCompany_save_button).setOnClickListener(view -> {
            s.setName(nameET.getText().toString());
            s.setRuc(rucET.getText().toString());
            s.setAddress(addressET.getText().toString());
            if(statusCB.isChecked())    s.setStatus("Activo");
            else    s.setStatus("Inactivo");
            //Firebase edit here
            update(s);
            //end edit save
            dialog.dismiss();
        });
        addCompanyPopup.findViewById(R.id.addCompany_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });
        //Set values of database into edit form
        nameET.setText(s.getName());
        rucET.setText(s.getRuc());
        addressET.setText(s.getAddress());
        statusCB.setChecked(s.getStatus().equals("Activo"));

    }

    public void createViewDialog(Company s){
        aBuilder = new AlertDialog.Builder(getContext());
        View addCompanyPopup = getLayoutInflater().inflate(R.layout.view_company, null);
        aBuilder.setView(addCompanyPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        nameTV = addCompanyPopup.findViewById(R.id.viewCompany_name);
        statusTV = addCompanyPopup.findViewById(R.id.viewCompany_status);
        rucTV = addCompanyPopup.findViewById(R.id.viewCompany_ruc);
        addressTV = addCompanyPopup.findViewById(R.id.viewCompany_address);
        addCompanyPopup.findViewById(R.id.viewCompany_ok_button).setOnClickListener(view -> {
            dialog.dismiss();
        });
        //Set values of database into edit form
        nameTV.setText(s.getName());
        rucTV.setText(s.getRuc());
        addressTV.setText(s.getAddress());
        statusTV.setText(s.getStatus());
    }

    public void update(Company s){
        db.getReference("empresa").child(s.getId()).updateChildren(s.toMap());
    }

    public void add(Company s){
        s.setId("E"+generateId());
        //db.getReference("empresa").push().setValue(s.toMap());
        db.getReference("empresa").child(s.getId()).setValue(s.toMap());
    }

    public void delete(Company s){
        db.getReference("empresa").child(s.getId()).updateChildren(s.toMap());
        //This is a physical remove
        //db.getReference("empresa").child(s.getId()).removeValue();
    }

    public String generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        String idS = "000"+idInt;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("empresa", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idS.substring(idS.length()-3);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        Company s = listCompany.get(item.getGroupId());
        switch (item.getItemId()){
            case 100:
                createViewDialog(s);
                break;
            case 101:
                createEditDialog(s);
                companyAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 102:
                if(s.getStatus().equals("Activo")) s.setStatus("Inactivo");
                else    s.setStatus("Activo");
                update(s);
                companyAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 103:
                s.setStatus("*");
                listCompany.remove(s);
                delete(s);
                companyAdapter.notifyItemRemoved(item.getGroupId());
                break;
        }
        order(sp.getSelectedItemPosition());
        return true;
    }
}