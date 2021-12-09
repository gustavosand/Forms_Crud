package com.dibujo.m_business.ui.tipodoc;

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
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.database.BackupList;
import com.dibujo.m_business.database.DocumentType;
import com.dibujo.m_business.database.adapters.DocumentTypeAdapter;
import com.dibujo.m_business.databinding.FragmentTipodocBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TipoDocFragment extends Fragment implements SearchView.OnQueryTextListener{

    private TipoDocViewModel tipodocViewModel;
    private FragmentTipodocBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    private Spinner sp;

    SearchView search;

    EditText nameET;
    CheckBox statusCB;

    FirebaseDatabase db;
    RecyclerView recyclerView;
    DocumentTypeAdapter documentTAdapter;
    ArrayList<DocumentType> listDocumentType;
    String idCount;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tipodocViewModel =
                new ViewModelProvider(this).get(TipoDocViewModel.class);

        binding = FragmentTipodocBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseDatabase.getInstance();

        binding.addDocumentTypeButton.setOnClickListener(view -> {
            createAddDialog();
        });

        search = binding.docSearch;
        search.setOnQueryTextListener(this);
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

        recyclerView = binding.tipodocRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listDocumentType = new ArrayList<>();
        documentTAdapter = new DocumentTypeAdapter(getContext(), listDocumentType);
        recyclerView.setAdapter(documentTAdapter);

        //Firebase code here
        db.getReference("documento_identidad").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDocumentType.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DocumentType dt = ds.getValue(DocumentType.class);
                    if(dt.getStatus().equals("*"))  continue;//ignore element whit status remove
                    dt.setId(ds.getKey());
                    listDocumentType.add(dt);
                }
                BackupList.listDocumentBackup.clear();
                BackupList.listDocumentBackup.addAll(listDocumentType);
                order(sp.getSelectedItemPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });
        //id
        db.getReference("idCont").child("documento_identidad").addValueEventListener(new ValueEventListener() {
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
        View addDocumentTypePopup = getLayoutInflater().inflate(R.layout.add_document, null);
        aBuilder.setView(addDocumentTypePopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        addDocumentTypePopup.findViewById(R.id.addDocumentType_save_button).setOnClickListener(view -> {
            nameET = addDocumentTypePopup.findViewById(R.id.addDocumentType_name);
            statusCB = addDocumentTypePopup.findViewById(R.id.addCompany_status);
            DocumentType nDT = new DocumentType();
            nDT.setName(nameET.getText().toString());
            if(statusCB.isChecked()){
                nDT.setStatus("Activo");
            }else{
                nDT.setStatus("Inactivo");
            }
            listDocumentType.add(nDT);
            //Firebase save here
            add(nDT);
            //end save
            dialog.dismiss();
        });
        addDocumentTypePopup.findViewById(R.id.addDocumentType_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
                Collections.sort(listDocumentType, DocumentType.docNameAZComparator);
                break;
            case 1:
                Collections.sort(listDocumentType, DocumentType.docNameZAComparator);
                break;
            case 2:
                Collections.sort(listDocumentType, DocumentType.docStatusComparator);
                break;

        }
        documentTAdapter.notifyDataSetChanged();
    }
    public void createEditDialog(DocumentType dt){
        aBuilder = new AlertDialog.Builder(getContext());
        View addDocumentTypePopup = getLayoutInflater().inflate(R.layout.add_document, null);
        aBuilder.setView(addDocumentTypePopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        nameET = addDocumentTypePopup.findViewById(R.id.addDocumentType_name);
        statusCB = addDocumentTypePopup.findViewById(R.id.addCompany_status);
        addDocumentTypePopup.findViewById(R.id.addDocumentType_save_button).setOnClickListener(view -> {
            dt.setName(nameET.getText().toString());
            if(statusCB.isChecked())    dt.setStatus("Activo");
            else    dt.setStatus("Inactivo");
            //Firebase edit here
            update(dt);
            //end edit save
            dialog.dismiss();
        });
        addDocumentTypePopup.findViewById(R.id.addDocumentType_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

        nameET.setText(dt.getName());
        statusCB.setChecked(dt.getStatus().equals("Activo"));

    }

    public void update(DocumentType dt){
        db.getReference("documento_identidad").child(dt.getId()).updateChildren(dt.toMap());
    }

    public void add(DocumentType dt){
        dt.setId("D"+generateId());
        //db.getReference("documento_identidad").push().setValue(dt.toMap());
        db.getReference("documento_identidad").child(dt.getId()).setValue(dt.toMap());
    }

    public void delete(DocumentType dt){
        db.getReference("documento_identidad").child(dt.getId()).updateChildren(dt.toMap());
        //This is a physical remove
        //db.getReference("documento_identidad").child(dt.getId()).removeValue();
    }

    public String generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        String idS = "000"+idInt;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("documento_identidad", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idS.substring(idS.length()-3);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        DocumentType dt = listDocumentType.get(item.getGroupId());
        switch (item.getItemId()){
            case 101:
                createEditDialog(dt);
                documentTAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 102:
                if(dt.getStatus().equals("Activo")) dt.setStatus("Inactivo");
                else    dt.setStatus("Activo");
                update(dt);
                documentTAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 103:
                dt.setStatus("*");
                listDocumentType.remove(dt);
                delete(dt);
                documentTAdapter.notifyItemRemoved(item.getGroupId());
                break;
        }

        BackupList.listDocumentBackup.clear();
        BackupList.listDocumentBackup.addAll(listDocumentType);
        order(sp.getSelectedItemPosition());
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        documentTAdapter.filtered(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        documentTAdapter.filtered(s);
        order(sp.getSelectedItemPosition());
        return false;
    }
}