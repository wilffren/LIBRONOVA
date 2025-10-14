package com.mycompany.libronova.domain;

import java.time.LocalDate;

/**
 * Domain model representing a library member.
 * 
 * @author Wilffren Muñoz
 */
public class Member extends User {
    
    private MemberStatus status;
    private LocalDate registrationDate;
    private String memberNumber;
    
    public Member() {
        super();
        this.role = UserRole.MEMBER;
    }
    
    public Member(String name, String email, String memberNumber) {
        super(name, email, UserRole.MEMBER);
        this.memberNumber = memberNumber;
        this.status = MemberStatus.ACTIVE;
        this.registrationDate = LocalDate.now();
    }
    
    // Getters and Setters
    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { 
        this.registrationDate = registrationDate; 
    }
    
    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }
    
    /**
     * Checks if the member is active and can borrow books.
     * 
     * @return true if member is active
     */
    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }
    
    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', memberNumber='%s', status=%s}",
                id, name, memberNumber, status);
    }
}