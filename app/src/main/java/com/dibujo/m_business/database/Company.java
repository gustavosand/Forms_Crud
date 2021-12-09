package com.dibujo.m_business.database;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Company {
    public String id;
    public String nombre;
    public String direccion;
    public String ruc;
    public String estado;


    public Company() {
    }

    public Company(String nombre, String direccion, String ruc, String estado) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ruc = ruc;
        this.estado = estado;
    }

    public String getStatus() {
        return estado;
    }

    public String getName() {
        return nombre;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public void setStatus(String estado) {
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return direccion;
    }

    public void setAddress(String direccion) {
        this.direccion = direccion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", getName());
        map.put("direccion", getAddress());
        map.put("ruc", getRuc());
        map.put("estado", getStatus());
        return map;
    }

    public static Comparator<Company> companyNameAZComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());
    public static Comparator<Company> companyNameZAComparator = (c1, c2) -> c2.getName().compareTo(c1.getName());
    public static Comparator<Company> companyStatusComparator = (c1, c2) -> c1.getStatus().compareTo(c2.getStatus());

}
