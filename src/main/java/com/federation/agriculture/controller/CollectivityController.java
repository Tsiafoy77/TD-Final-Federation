package com.federation.agriculture.controller;

import com.federation.agriculture.dto.*;
import com.federation.agriculture.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    // A - POST /collectivities
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CollectivityDTO> createCollectivities(@RequestBody List<CreateCollectivityDTO> requestBody) {
        return collectivityService.createCollectivities(requestBody);
    }

    // J - PUT /collectivities/{id}/identity
    @PutMapping("/{id}/identity")
    public CollectivityDTO assignIdentity(
            @PathVariable String id,
            @RequestBody CollectivityIdentityDTO identity) {
        return collectivityService.assignIdentity(id, identity);
    }

    // C - GET /collectivities/{id}/membershipFees
    @GetMapping("/{id}/membershipFees")
    public List<MembershipFeeDTO> getMembershipFees(@PathVariable String id) {
        return collectivityService.getMembershipFees(id);
    }

    // C - POST /collectivities/{id}/membershipFees
    @PostMapping("/{id}/membershipFees")
    public List<MembershipFeeDTO> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeDTO> fees) {
        return collectivityService.createMembershipFees(id, fees);
    }


    // FONCTIONNALITÉ D - Transactions des collectivités

    @GetMapping("/{id}/transactions")
    public List<CollectivityTransactionDTO> getTransactions(
            @PathVariable String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return collectivityService.getTransactions(id, from, to);
    }
}