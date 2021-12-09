package com.dibujo.m_business.ui.form;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dibujo.m_business.R;
import com.dibujo.m_business.database.BackupList;
import com.dibujo.m_business.database.Company;
import com.dibujo.m_business.database.DocumentType;
import com.dibujo.m_business.database.Form;
import com.dibujo.m_business.database.Service;
import com.dibujo.m_business.database.adapters.FormAdapter;
import com.dibujo.m_business.databinding.FragmentFormBinding;
import com.dibujo.m_business.ui.form.FormViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FormFragment extends Fragment implements SearchView.OnQueryTextListener{

    private FormViewModel formViewModel;
    private FragmentFormBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    private Spinner sp;

    SearchView search;

    EditText nameET;
    EditText lastNameET;
    EditText mothersLastNameET;
    EditText positionET;
    EditText emailET;
    EditText telephoneET;
    Spinner serviceS;
    Spinner companyS;
    EditText descriptionET;
    TextView dateDefaultTV;
    Spinner docS;
    EditText docNumberET;
    CheckBox statusCB;

    String serviceT, companyT, documentT, dateT;

    TextView nameTV, lastNameTV, mothersLastNamesTV, positionTV, emailTV, telephoneTV, serviceTV, companyTV, descriptionTV, docTV, docNumberTV, dateTV, statusTV;

    FirebaseDatabase db;
    RecyclerView recyclerView;
    FormAdapter formAdapter;
    ArrayList<Form> listForm;
    ArrayList<DocumentType> listDocuments;
    ArrayList<Service> listServices;
    ArrayList<Company> listCompanies;
    String[] optionsService, optionsDocument, optionsCompany;
    String idCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        formViewModel =
                new ViewModelProvider(this).get(FormViewModel.class);

        binding = FragmentFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseDatabase.getInstance();

        binding.addFormButton.setOnClickListener(view -> {
            createAddDialog();
        });

        search = binding.formSearch;
        search.setOnQueryTextListener(this);
        sp = binding.spinnerOrder;

        String[] optionsOrder = {"Nombre A-Z","Nombre Z-A", "Fecha reciente", "Fecha antiguo", "Estado"};
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

        recyclerView = binding.formRecyclerview;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listForm = new ArrayList<>();
        listDocuments = new ArrayList<>();
        listServices = new ArrayList<>();
        listCompanies = new ArrayList<>();
        formAdapter = new FormAdapter(getContext(), listForm);
        recyclerView.setAdapter(formAdapter);

        //Firebase code here
        db.getReference("ficha").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listForm.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Form s = ds.getValue(Form.class);
                    if(s.getStatus().equals("*"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());

                    listForm.add(s);
                }
                BackupList.listFormBackup.clear();
                BackupList.listFormBackup.addAll(listForm);
                order(sp.getSelectedItemPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });
        //id
        db.getReference("idCont").child("ficha").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idCount = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error+"");
            }
        });

        db.getReference("servicio").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listServices.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Service s = ds.getValue(Service.class);
                    if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listServices.add(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        db.getReference("documento_identidad").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDocuments.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DocumentType s = ds.getValue(DocumentType.class);
                    if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listDocuments.add(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        db.getReference("empresa").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCompanies.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Company s = ds.getValue(Company.class);
                    if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listCompanies.add(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
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
        View addFormPopup = getLayoutInflater().inflate(R.layout.add_form, null);
        aBuilder.setView(addFormPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        //Date recover and format
        Date date = new Date();
        Date enrollmentDate = new Date(date.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        dateT = formatDate.format(enrollmentDate);
        //end
        nameET = addFormPopup.findViewById(R.id.addForm_name);
        lastNameET = addFormPopup.findViewById(R.id.addForm_lastName);
        mothersLastNameET = addFormPopup.findViewById(R.id.addForm_mothersLastName);
        statusCB = addFormPopup.findViewById(R.id.addForm_status);
        positionET = addFormPopup.findViewById(R.id.addForm_position);
        emailET = addFormPopup.findViewById(R.id.addForm_email);
        telephoneET = addFormPopup.findViewById(R.id.addForm_telephone);
        descriptionET = addFormPopup.findViewById(R.id.addForm_description);
        docNumberET = addFormPopup.findViewById(R.id.addForm_docNumber);
        dateDefaultTV = addFormPopup.findViewById(R.id.addForm_dateView);

        addFormPopup.findViewById(R.id.addForm_save_button).setOnClickListener(view -> {

            Form nS = new Form();
            nS.setName(nameET.getText().toString());
            if(statusCB.isChecked()){
                nS.setStatus("Activo");
            }else{
                nS.setStatus("Inactivo");
            }
            nS.setLastName(lastNameET.getText().toString());
            nS.setMothersLastName(mothersLastNameET.getText().toString());
            nS.setPosition(positionET.getText().toString());
            nS.setEmail(emailET.getText().toString());
            nS.setTelephone(telephoneET.getText().toString());
            nS.setDescription(descriptionET.getText().toString());
            nS.setDocumentNumber(docNumberET.getText().toString());
            nS.setService(serviceT);
            nS.setDocument(documentT);
            nS.setCompany(companyT);
            nS.setEnrollmentDate(dateT);

            listForm.add(nS);
            //Firebase save here
            add(nS);
            //end save
            dialog.dismiss();
        });
        addFormPopup.findViewById(R.id.addForm_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

        docS = addFormPopup.findViewById(R.id.spinnerDoc);
        serviceS = addFormPopup.findViewById(R.id.spinnerSer);
        companyS = addFormPopup.findViewById(R.id.spinnerCom);



        optionsService = new String[listServices.size()];
        for (int i = 0;i<listServices.size();++i){
            optionsService[i] = listServices.get(i).getName();
        }
        ArrayAdapter<String> optionsServiceAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsService);
        serviceS.setAdapter(optionsServiceAdapter);
        serviceS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceT = listServices.get(i).getId();
                Log.e("RESULTADO", serviceT+" --> "+listServices.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        optionsDocument = new String[listDocuments.size()];
        for (int i = 0;i<listDocuments.size();++i){
            optionsDocument[i] = listDocuments.get(i).getName();
        }
        ArrayAdapter<String> optionsDocumentAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsDocument);
        docS.setAdapter(optionsDocumentAdapter);
        docS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                documentT = listDocuments.get(i).getId();
                Log.e("RESULTADO", documentT+" --> "+listDocuments.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        optionsCompany = new String[listCompanies.size()];
        for (int i = 0;i<listCompanies.size();++i){
            optionsCompany[i] = listCompanies.get(i).getName();
        }
        ArrayAdapter<String> optionsCompanyAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsCompany);
        companyS.setAdapter(optionsCompanyAdapter);
        companyS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                companyT = listCompanies.get(i).getId();
                Log.e("RESULTADO", companyT+" --> "+listCompanies.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        dateDefaultTV.setText("Fecha: "+dateT);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
                Collections.sort(listForm, Form.formNameAZComparator);
                break;
            case 1:
                Collections.sort(listForm, Form.formNameZAComparator);
                break;
            case 2:
                Collections.sort(listForm, Form.formDateAZComparator);
                break;
            case 3:
                Collections.sort(listForm, Form.formDateZAComparator);
                break;
            case 4:
                Collections.sort(listForm, Form.formStatusComparator);
                break;

        }
        formAdapter.notifyDataSetChanged();
    }

    public void createEditDialog(Form s){
        aBuilder = new AlertDialog.Builder(getContext());
        View addFormPopup = getLayoutInflater().inflate(R.layout.add_form, null);
        aBuilder.setView(addFormPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();

        docS = addFormPopup.findViewById(R.id.spinnerDoc);
        serviceS = addFormPopup.findViewById(R.id.spinnerSer);
        companyS = addFormPopup.findViewById(R.id.spinnerCom);

        dateDefaultTV = addFormPopup.findViewById(R.id.addForm_dateView);

        nameET = addFormPopup.findViewById(R.id.addForm_name);
        lastNameET = addFormPopup.findViewById(R.id.addForm_lastName);
        mothersLastNameET = addFormPopup.findViewById(R.id.addForm_mothersLastName);
        statusCB = addFormPopup.findViewById(R.id.addForm_status);
        positionET = addFormPopup.findViewById(R.id.addForm_position);
        emailET = addFormPopup.findViewById(R.id.addForm_email);
        telephoneET = addFormPopup.findViewById(R.id.addForm_telephone);
        descriptionET = addFormPopup.findViewById(R.id.addForm_description);
        docNumberET = addFormPopup.findViewById(R.id.addForm_docNumber);

        addFormPopup.findViewById(R.id.addForm_save_button).setOnClickListener(view -> {

            s.setName(nameET.getText().toString());
            if(statusCB.isChecked()){
                s.setStatus("Activo");
            }else{
                s.setStatus("Inactivo");
            }
            s.setLastName(lastNameET.getText().toString());
            s.setMothersLastName(mothersLastNameET.getText().toString());
            s.setPosition(positionET.getText().toString());
            s.setEmail(emailET.getText().toString());
            s.setTelephone(telephoneET.getText().toString());
            s.setDescription(descriptionET.getText().toString());
            s.setDocumentNumber(docNumberET.getText().toString());
            s.setService(serviceT);
            s.setDocument(documentT);
            s.setCompany(companyT);
            //Firebase save here
            update(s);
            //end save
            dialog.dismiss();
        });
        addFormPopup.findViewById(R.id.addForm_cancel_button).setOnClickListener(view -> {
            dialog.dismiss();
        });

        optionsService = new String[listServices.size()];
        for (int i = 0;i<listServices.size();++i){
            optionsService[i] = listServices.get(i).getName();
        }
        ArrayAdapter<String> optionsServiceAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsService);
        serviceS.setAdapter(optionsServiceAdapter);
        serviceS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceT = listServices.get(i).getId();
                Log.e("RESULTADO", serviceT+" --> "+listServices.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        optionsDocument = new String[listDocuments.size()];
        for (int i = 0;i<listDocuments.size();++i){
            optionsDocument[i] = listDocuments.get(i).getName();
        }
        ArrayAdapter<String> optionsDocumentAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsDocument);
        docS.setAdapter(optionsDocumentAdapter);
        docS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                documentT = listDocuments.get(i).getId();
                Log.e("RESULTADO", documentT+" --> "+listDocuments.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        optionsCompany = new String[listCompanies.size()];
        for (int i = 0;i<listCompanies.size();++i){
            optionsCompany[i] = listCompanies.get(i).getName();
        }
        ArrayAdapter<String> optionsCompanyAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsCompany);
        companyS.setAdapter(optionsCompanyAdapter);
        companyS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                companyT = listCompanies.get(i).getId();
                Log.e("RESULTADO", companyT+" --> "+listCompanies.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Set data into edit form

        nameET.setText(s.getName());
        lastNameET.setText(s.getLastName());
        mothersLastNameET.setText(s.getMothersLastName());
        positionET.setText(s.getPosition());
        emailET.setText(s.getEmail());
        telephoneET.setText(s.getTelephone());
        docNumberET.setText(s.getDocumentNumber());
        descriptionET.setText(s.getDescription());
        dateDefaultTV.setText("Fecha: "+s.getEnrollmentDate());
        statusCB.setChecked(s.getStatus().equals("Activo"));

        //Set spinners
        for (int i = 0;i<listServices.size();++i){
            if(listServices.get(i).getId().equals(s.getService())){
                serviceS.setSelection(i);
                break;
            }
        }

        for (int i = 0;i<listCompanies.size();++i){
            if(listCompanies.get(i).getId().equals(s.getCompany())){
                companyS.setSelection(i);
            }
        }

        for (int i = 0;i<listDocuments.size();++i){
            if(listDocuments.get(i).getId().equals(s.getDocument())){
                docS.setSelection(i);
            }
        }

    }

    public void createViewDialog(Form s){
        aBuilder = new AlertDialog.Builder(getContext());
        View addFormPopup = getLayoutInflater().inflate(R.layout.view_form, null);
        aBuilder.setView(addFormPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        nameTV = addFormPopup.findViewById(R.id.viewForm_name);
        lastNameTV = addFormPopup.findViewById(R.id.viewForm_lastName);
        mothersLastNamesTV = addFormPopup.findViewById(R.id.viewForm_mothersLastName);
        positionTV = addFormPopup.findViewById(R.id.viewForm_position);
        emailTV = addFormPopup.findViewById(R.id.viewForm_email);
        telephoneTV = addFormPopup.findViewById(R.id.viewForm_telephone);
        docNumberTV = addFormPopup.findViewById(R.id.viewForm_docNumber);
        serviceTV = addFormPopup.findViewById(R.id.viewForm_service);
        companyTV = addFormPopup.findViewById(R.id.viewForm_company);
        descriptionTV = addFormPopup.findViewById(R.id.viewForm_description);
        dateTV = addFormPopup.findViewById(R.id.viewForm_date);
        statusTV = addFormPopup.findViewById(R.id.viewForm_status);
        addFormPopup.findViewById(R.id.viewForm_ok_button).setOnClickListener(view -> {
            dialog.dismiss();
        });
        //Set values of database into view
        nameTV.setText(s.getName());
        lastNameTV.setText(s.getLastName());
        mothersLastNamesTV.setText(s.getMothersLastName());
        positionTV.setText(s.getPosition());
        emailTV.setText(s.getEmail());
        telephoneTV.setText(s.getTelephone());
        docNumberTV.setText(s.getDocumentName()+" N°: " + s.getDocumentNumber());
        serviceTV.setText(s.getServiceName());
        companyTV.setText(s.getCompanyName());
        descriptionTV.setText(s.getDescription());
        dateTV.setText(s.getEnrollmentDate());
        statusTV.setText(s.getStatus());
    }

    public void update(Form s){
        db.getReference("ficha").child(s.getId()).updateChildren(s.toMap());
    }

    public void add(Form s){
        s.setId("F"+generateId());
        //db.getReference("ficha").push().setValue(s.toMap());
        db.getReference("ficha").child(s.getId()).setValue(s.toMap());
    }

    public void delete(Form s){
        db.getReference("ficha").child(s.getId()).updateChildren(s.toMap());
        //This is a physical remove
        //db.getReference("ficha").child(s.getId()).removeValue();
    }

    public String generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        String idS = "000"+idInt;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("ficha", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idS.substring(idS.length()-3);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        Form s = listForm.get(item.getGroupId());
        switch (item.getItemId()){
            case 100:
                createViewDialog(s);
                break;
            case 101:
                createEditDialog(s);
                formAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 102:
                if(s.getStatus().equals("Activo")) s.setStatus("Inactivo");
                else    s.setStatus("Activo");
                update(s);
                formAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 103:
                s.setStatus("*");
                listForm.remove(s);
                delete(s);
                formAdapter.notifyItemRemoved(item.getGroupId());
                break;
        }
        BackupList.listFormBackup.clear();
        BackupList.listFormBackup.addAll(listForm);
        order(sp.getSelectedItemPosition());
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        formAdapter.filtered(s);
        order(sp.getSelectedItemPosition());
        return false;
    }
}
//using date...(DELETE THIS COMMENT)
        /*db.getReference("servicio").child(s.getService()).child("nombre").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DataSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        s.setServiceName(document.getValue(String.class));
                                        Log.e("BIEN", "DataSnapshot data: " + document.getValue());
                                    } else {
                                        Log.e("ERROR", "No such document");
                                    }
                                } else {
                                    Log.e("ERROR", "get failed with ", task.getException());
                                }
                            });*/