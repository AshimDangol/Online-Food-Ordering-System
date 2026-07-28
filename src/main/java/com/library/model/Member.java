package com.library.model;

public class Member {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String memberType;
    private String role;

    public Member(String id, String name, String email, String phone, String memberType, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memberType = memberType;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMemberType() { return memberType; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return String.format("Member[ID=%s, Name='%s', Email=%s, Phone=%s, Type=%s, Role=%s]",
                id, name, email, phone, memberType, role);
    }
}
