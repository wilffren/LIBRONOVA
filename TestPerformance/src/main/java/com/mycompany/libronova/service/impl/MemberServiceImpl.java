package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.domain.MemberStatus;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.repository.MemberRepository;
import com.mycompany.libronova.service.MemberService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of MemberService with business validation.
 * 
 * @author Wilffren Muñoz
 */
public class MemberServiceImpl implements MemberService {
    
    private static final Logger LOGGER = Logger.getLogger(MemberServiceImpl.class.getName());
    private final MemberRepository memberRepository;
    
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    @Override
    public Member registerMember(Member member) throws ValidationException, DatabaseException {
        validateMember(member);
        
        LOGGER.info("Registering member: " + member.getMemberNumber());
        return memberRepository.save(member);
    }
    
    @Override
    public Member updateMember(Member member) throws EntityNotFoundException, ValidationException, DatabaseException {
        validateMember(member);
        
        if (member.getId() == null) {
            throw new ValidationException("Member ID is required for update");
        }
        
        // Verify member exists
        memberRepository.findById(member.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member", member.getId()));
        
        LOGGER.info("Updating member: " + member.getId());
        return memberRepository.update(member);
    }
    
    @Override
    public Member findMemberById(Long id) throws EntityNotFoundException, DatabaseException {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member", id));
    }
    
    @Override
    public Member findMemberByNumber(String memberNumber) throws EntityNotFoundException, DatabaseException {
        return memberRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new EntityNotFoundException("Member", memberNumber));
    }
    
    @Override
    public List<Member> listAllMembers() throws DatabaseException {
        return memberRepository.findAll();
    }
    
    @Override
    public List<Member> listActiveMembers() throws DatabaseException {
        return memberRepository.findAllActive();
    }
    
    @Override
    public void activateMember(Long id) throws EntityNotFoundException, DatabaseException {
        Member member = findMemberById(id);
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.update(member);
        
        LOGGER.info("Member activated: " + id);
    }
    
    @Override
    public void deactivateMember(Long id) throws EntityNotFoundException, DatabaseException {
        Member member = findMemberById(id);
        member.setStatus(MemberStatus.INACTIVE);
        memberRepository.update(member);
        
        LOGGER.info("Member deactivated: " + id);
    }
    
    /**
     * Validates member data.
     */
    private void validateMember(Member member) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            errors.add("Name is required");
        }
        
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        }
        
        if (member.getMemberNumber() == null || member.getMemberNumber().trim().isEmpty()) {
            errors.add("Member number is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}