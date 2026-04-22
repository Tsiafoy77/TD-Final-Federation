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
    public List&lt;MemberDTO&gt; createMembers(List&lt;CreateMemberDTO&gt; createDTOs) {
        List&lt;MemberDTO&gt; result = new ArrayList&lt;&gt;();
        for (CreateMemberDTO dto : createDTOs) {
            if (dto.getReferees() == null || dto.getReferees().size() &lt; 2) {
                throw new RuntimeException(&quot;Un membre doit avoir au moins 2
                parrains&quot;);
            }
            if (!dto.isRegistrationFeePaid() || !dto.isMembershipDuesPaid()) {
                throw new RuntimeException(&quot;Les frais d&#39;adhésion doivent être
                payés&quot;);
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
            List&lt;Member&gt; referees =
                    memberRepository.findAllByIds(dto.getReferees());
            saved.setReferees(referees);
            result.add(MemberDTO.fromMember(saved));
        }
        return result;
    }
}