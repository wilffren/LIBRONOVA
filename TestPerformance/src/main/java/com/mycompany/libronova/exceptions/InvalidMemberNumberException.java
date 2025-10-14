package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when a member number contains invalid characters.
 * Member numbers should only contain numeric characters.
 * 
 * @author Wilffren Muñoz
 */
public class InvalidMemberNumberException extends LibroNovaException {
    
    private final String memberNumber;
    
    /**
     * Creates a new InvalidMemberNumberException with the invalid member number.
     * 
     * @param memberNumber the invalid member number
     */
    public InvalidMemberNumberException(String memberNumber) {
        super(String.format("Invalid member number '%s'. Member numbers must contain only numeric characters.", 
                memberNumber));
        this.memberNumber = memberNumber;
    }
    
    /**
     * Gets the invalid member number that caused this exception.
     * 
     * @return the invalid member number
     */
    public String getMemberNumber() {
        return memberNumber;
    }
}