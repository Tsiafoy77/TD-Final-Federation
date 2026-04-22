package com.federation.agriculture.service;

import com.federation.agriculture.dto.CreateMemberDTO;
import com.federation.agriculture.dto.MemberDTO;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.MemberRepository;
import java.time.LocalDate;
import java.util.*;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberDTO> createMembers(List<CreateMemberDTO> createDTOs) {
        List<MemberDTO> result = new ArrayList<>();

        for (CreateMemberDTO dto : createDTOs) {
            if (dto.getReferees() == null || dto.getReferees().size() < 2) {
                throw new RuntimeException("Un membre doit avoir au moins 2 parrains");
            }

            if (!dto.isRegistrationFeePaid() || !dto.isMembershipDuesPaid()) {
                throw new RuntimeException("Les frais d'adhésion doivent être payés");
            }

            Member member = new Member();
            member.setFirstName(dto.getFirstName());
            member.setLastName(dto.getLastName());
            member.setBirthDate(dto.getBirthDate());
            member.setGender(dto.getGender());
            member.setAddress(dto.getAddress());
            member.setProfession(dto.getProfession());
            member.setPhoneNumber(dto.getPhoneNumber());
            member.setEmail(dto.getEmail());
            member.setOccupation(dto.getOccupation());
            member.setCollectivityId(dto.getCollectivityIdentifier());
            member.setRegistrationFeePaid(dto.isRegistrationFeePaid());
            member.setMembershipDuesPaid(dto.isMembershipDuesPaid());
            member.setMembershipDate(LocalDate.now());
            member.setRefereesIds(dto.getReferees());

            Member saved = memberRepository.save(member);

            List<Member> referees = memberRepository.findAllByIds(dto.getReferees());
            saved.setReferees(referees);

            result.add(MemberDTO.fromMember(saved));
        }

        return result;
    }
}