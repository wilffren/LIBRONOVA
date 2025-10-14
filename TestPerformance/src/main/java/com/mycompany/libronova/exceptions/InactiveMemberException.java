package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when an inactive member attempts to borrow a book.
 * 
 * @author Wilffren Muñoz
 */
public class InactiveMemberException extends LibroNovaException {
    
    private final String memberNumber;
    
    public InactiveMemberException(String memberNumber) {
        super(String.format("The member with number '%s' is not active", memberNumber));
        this.memberNumber = memberNumber;
    }
    
    public String getMemberNumber() {
        return memberNumber;
    }
}