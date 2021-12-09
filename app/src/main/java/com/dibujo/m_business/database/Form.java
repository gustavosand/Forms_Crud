package com.dibujo.m_business.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Form {
    public String id;
    public String nombre;
    public String apellido_paterno;
    public String apellido_materno;
    public String cargo;
    public String correo;
    public String estado;
    public String telefono;
    public String servicio;
    public String empresa;
    public String descripcion;
    public String documento;
    public String numero_documento;
    public String fecha_inscripcion;

    public String serviceName;
    public String companyName;
    public String documentName;

    public Form() {
    }

    public Form(String nombre, String apellido_paterno, String apellido_materno, String cargo, String correo, String estado, String telefono, String servicio, String empresa, String descripcion, String documento, String numero_documento) {
        this.nombre = nombre;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.cargo = cargo;
        this.correo = correo;
        this.estado = estado;
        this.telefono = telefono;
        this.servicio = servicio;
        this.empresa = empresa;
        this.descripcion = descripcion;
        this.documento = documento;
        this.numero_documento = numero_documento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return nombre;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public String getLastName() {
        return apellido_paterno;
    }

    public void setLastName(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getMothersLastName() {
        return apellido_materno;
    }

    public void setMothersLastName(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getPosition() {
        return cargo;
    }

    public void setPosition(String cargo) {
        this.cargo = cargo;
    }

    public String getEmail() {
        return correo;
    }

    public void setEmail(String correo) {
        this.correo = correo;
    }

    public String getStatus() {
        return estado;
    }

    public void setStatus(String estado) {
        this.estado = estado;
    }

    public String getTelephone() {
        return telefono;
    }

    public void setTelephone(String telefono) {
        this.telefono = telefono;
    }

    public String getService() {
        return servicio;
    }

    public void setService(String servicio) {
        this.servicio = servicio;
    }

    public String getCompany() {
        return empresa;
    }

    public void setCompany(String empresa) {
        this.empresa = empresa;
    }

    public String getDescription() {
        return descripcion;
    }

    public void setDescription(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDocument() {
        return documento;
    }

    public void setDocument(String documento) {
        this.documento = documento;
    }

    public String getDocumentNumber() {
        return numero_documento;
    }

    public void setDocumentNumber(String numero_documento) {
        this.numero_documento = numero_documento;
    }

    public String getEnrollmentDate() {
        return fecha_inscripcion;
    }

    public void setEnrollmentDate(String fecha_inscripcion) {
        this.fecha_inscripcion = fecha_inscripcion;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", getName());
        map.put("apellido_paterno", getLastName());
        map.put("apellido_materno", getMothersLastName());
        map.put("cargo", getPosition());
        map.put("correo", getEmail());
        map.put("estado", getStatus());
        map.put("telefono", getTelephone());
        map.put("servicio", getService());
        map.put("empresa", getCompany());
        map.put("descripcion", getDescription());
        map.put("documento", getDocument());
        map.put("numero_documento", getDocumentNumber());
        map.put("fecha_inscripcion", getEnrollmentDate());

        return map;
    }

    public static Comparator<Form> formNameAZComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());
    public static Comparator<Form> formNameZAComparator = (c1, c2) -> c2.getName().compareTo(c1.getName());
    public static Comparator<Form> formStatusComparator = (c1, c2) -> c1.getStatus().compareTo(c2.getStatus());
    public static Comparator<Form> formDateZAComparator = (f1, f2) -> {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(f1.getEnrollmentDate());
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(f2.getEnrollmentDate());
            return start.compareTo(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };
    public static Comparator<Form> formDateAZComparator = (f1, f2) -> {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(f1.getEnrollmentDate());
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(f2.getEnrollmentDate());
            return end.compareTo(start);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };
}

