package com.federation.agriculture.service;

import com.federation.agriculture.dto.CreateMemberDTO;
import com.federation.agriculture.dto.CreateMemberPaymentDTO;
import com.federation.agriculture.dto.MemberDTO;
import com.federation.agriculture.dto.MemberPaymentDTO;
import com.federation.agriculture.dto.MembershipFeeDTO;
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
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new RuntimeException("Member not found: " + memberId);
        }

        // Vérifier que le membre appartient à une collectivité
        String collectivityId = member.getCollectivityId();
        if (collectivityId == null || collectivityId.isEmpty()) {
            throw new RuntimeException("Member is not associated with any collectivity");
        }

        List<MemberPaymentDTO> result = new ArrayList<>();

        for (CreateMemberPaymentDTO payment : payments) {
            // Vérifier que le membership fee existe
            MembershipFeeDTO fee = membershipFeeRepository.findById(payment.getMembershipFeeIdentifier());
            if (fee == null) {
                throw new RuntimeException("Membership fee not found: " + payment.getMembershipFeeIdentifier());
            }

            // Vérifier le montant
            if (payment.getAmount() <= 0) {
                throw new RuntimeException("Amount must be greater than 0");
            }

            // Vérifier le mode de paiement
            String paymentMode = payment.getPaymentMode();
            if (paymentMode == null || (!paymentMode.equals("CASH") && !paymentMode.equals("MOBILE_BANKING") &&
                    !paymentMode.equals("BANK_TRANSFER"))) {
                throw new RuntimeException("Invalid payment mode: " + paymentMode);
            }

            // Créer le paiement
            MemberPaymentDTO savedPayment = memberPaymentRepository.save(memberId, payment, payment.getMembershipFeeIdentifier());
            result.add(savedPayment);

            // Créer la transaction associée dans la collectivité
            collectivityTransactionRepository.save(
                    collectivityId,
                    memberId,
                    payment.getAmount(),
                    payment.getPaymentMode(),
                    payment.getAccountCreditedIdentifier(),
                    LocalDate.now()
            );
        }

        return result;
    }
}