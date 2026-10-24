package com.ahmed.firstaidhero1.model;

public class Contact {
    private final long id;
    private final String name;
    private final String phone;
    private final boolean builtIn;

    public Contact(long id, String name, String phone, boolean builtIn) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.builtIn = builtIn;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public boolean isBuiltIn() { return builtIn; }
}