package com.library.factory;

import com.library.model.Member;

public class AdminFactory extends MemberFactory {
    @Override
    public Member createMember(String id, String name, String email, String phone, String memberType) {
        return new Member(id, name, email, phone, memberType, "ADMIN");
    }
}
