package com.library.factory;

import com.library.model.Member;

public abstract class MemberFactory {
    public abstract Member createMember(String id, String name, String email, String phone, String memberType);

    public Member createAndDisplay(String id, String name, String email, String phone, String memberType) {
        Member member = createMember(id, name, email, phone, memberType);
        System.out.println("  Registered: " + member);
        return member;
    }
}
