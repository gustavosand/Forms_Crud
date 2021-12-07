package com.dibujo.m_business.database;

import com.dibujo.m_business.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DocumentType {
    public String id;
    public String nombre;
    public String estado;


    public DocumentType() {
    }

    public DocumentType(String nombre, String estado) {
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

    public static Comparator<DocumentType> docNameAZComparator = (d1, d2) -> d1.getName().compareTo(d2.getName());
    public static Comparator<DocumentType> docNameZAComparator = (d1, d2) -> d2.getName().compareTo(d1.getName());
    public static Comparator<DocumentType> docStatusComparator = (d1, d2) -> d1.getStatus().compareTo(d2.getStatus());


}
