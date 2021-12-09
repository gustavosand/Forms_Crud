package com.dibujo.m_business.database;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Service {
    public String id;
    public String nombre;
    public String estado;


    public Service() {
    }

    public Service(String nombre, String estado) {
        this.nombre = nombre;
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

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", getName());
        map.put("estado", getStatus());
        return map;
    }

    public static Comparator<Service> serviceNameAZComparator = (s1, s2) -> s1.getName().compareTo(s2.getName());
    public static Comparator<Service> serviceNameZAComparator = (s1, s2) -> s2.getName().compareTo(s1.getName());
    public static Comparator<Service> serviceStatusComparator = (s1, s2) -> s1.getStatus().compareTo(s2.getStatus());


}
