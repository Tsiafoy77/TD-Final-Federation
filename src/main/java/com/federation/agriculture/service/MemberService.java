package com.federation.agriculture.service;

import com.federation.agriculture.dto.CreateMemberDTO;
import com.federation.agriculture.dto.CreateMemberPaymentDTO;
import com.federation.agriculture.dto.MemberDTO;
import com.federation.agriculture.dto.MemberPaymentDTO;
import com.federation.agriculture.dto.MembershipFeeDTO;
import com.federation.agriculture.exception.BadRequestException;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.repository.CollectivityTransactionRepository;
import com.federation.agriculture.repository.MemberPaymentRepository;
import com.federation.agriculture.repository.MemberRepository;
import com.federation.agriculture.repository.MembershipFeeRepository;
import java.time.LocalDate;
import java.util.*;

public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final CollectivityTransactionRepository collectivityTransactionRepository;
    private final MembershipFeeRepository membershipFeeRepository;

    public MemberService(MemberRepository memberRepository,
                         MemberPaymentRepository memberPaymentRepository,
                         CollectivityTransactionRepository collectivityTransactionRepository,
                         MembershipFeeRepository membershipFeeRepository) {
        this.memberRepository = memberRepository;
        this.memberPaymentRepository = memberPaymentRepository;
        this.collectivityTransactionRepository = collectivityTransactionRepository;
        this.membershipFeeRepository = membershipFeeRepository;
    }


    // FONCTIONNALITÉ B - Création d'un membre

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

    // FONCTIONNALITÉ D - Création des paiements
    public List<MemberPaymentDTO> createPayments(String memberId, List<CreateMemberPaymentDTO> payments) {
        // ✅ 404 si le membre n'existe pas
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new RuntimeException("Member not found: " + memberId);
        }

        // ✅ 404 si le membre n'a pas de collectivité
        String collectivityId = member.getCollectivityId();
        if (collectivityId == null || collectivityId.isEmpty()) {
            throw new RuntimeException("Member is not associated with any collectivity");
        }

        List<MemberPaymentDTO> result = new ArrayList<>();

        for (CreateMemberPaymentDTO payment : payments) {
            // ✅ 404 si le membership fee n'existe pas
            MembershipFeeDTO fee = membershipFeeRepository.findById(payment.getMembershipFeeIdentifier());
            if (fee == null) {
                throw new RuntimeException("Membership fee not found: " + payment.getMembershipFeeIdentifier());
            }

            // ✅ 400 si montant négatif
            if (payment.getAmount() <= 0) {
                throw new BadRequestException("Amount must be greater than 0");
            }

            // ✅ 400 si mode de paiement invalide
            String paymentMode = payment.getPaymentMode();
            if (paymentMode == null || (!paymentMode.equals("CASH") && !paymentMode.equals("MOBILE_BANKING") &&
                    !paymentMode.equals("BANK_TRANSFER"))) {
                throw new BadRequestException("Invalid payment mode: " + paymentMode);
            }

            // Créer le paiement...
        }

        return result;
    }
}