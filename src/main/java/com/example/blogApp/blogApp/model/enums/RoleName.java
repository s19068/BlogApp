package com.example.blogApp.blogApp.model.enums;

public enum RoleName {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private String prefixedName;

    RoleName(String prefixedName) {
        this.prefixedName = prefixedName;
    }
    public String getPrefixedName() {return prefixedName;}
}
