package com.federation.agriculture.controller;

import com.federation.agriculture.dto.CreateMemberDTO;
import com.federation.agriculture.dto.CreateMemberPaymentDTO;
import com.federation.agriculture.dto.MemberDTO;
import com.federation.agriculture.dto.MemberPaymentDTO;
import com.federation.agriculture.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // A - POST /members
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<MemberDTO> createMembers(@RequestBody List<CreateMemberDTO> requestBody) {
        return memberService.createMembers(requestBody);
    }


    // FONCTIONNALITÉ D - Création des paiements
    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MemberPaymentDTO> createPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentDTO> payments) {
        return memberService.createPayments(id, payments);
    }
}