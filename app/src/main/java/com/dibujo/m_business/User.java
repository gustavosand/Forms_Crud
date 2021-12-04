package com.dibujo.m_business;

import java.util.Comparator;

public class User {

    String name, code;

    public User(String n, String c) {
        name = n;
        code = c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static Comparator<User> userNameAZComparator = (u1, u2) -> u1.getName().compareTo(u2.getName());
    public static Comparator<User> userNameZAComparator = (u1, u2) -> u2.getName().compareTo(u1.getName());
    public static Comparator<User> userCodeComparator = (u1, u2) -> u1.getCode().compareTo(u2.getCode());

}
