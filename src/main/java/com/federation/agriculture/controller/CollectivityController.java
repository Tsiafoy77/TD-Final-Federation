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

    @GetMapping
    public List<CollectivityDTO> getAllCollectivities() {
        return collectivityService.getAllCollectivities();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CollectivityDTO> createCollectivities(@RequestBody List<CreateCollectivityDTO> requestBody) {
        return collectivityService.createCollectivities(requestBody);
    }

    @PutMapping("/{id}/informations")
    public CollectivityDTO assignIdentity(@PathVariable String id, @RequestBody CollectivityIdentityDTO identity) {
        return collectivityService.assignIdentity(id, identity);
    }

    @GetMapping("/{id}/membershipFees")
    public List<MembershipFeeDTO> getMembershipFees(@PathVariable String id) {
        return collectivityService.getMembershipFees(id);
    }

    @PostMapping("/{id}/membershipFees")
    public List<MembershipFeeDTO> createMembershipFees(@PathVariable String id, @RequestBody List<CreateMembershipFeeDTO> fees) {
        return collectivityService.createMembershipFees(id, fees);
    }

    @GetMapping("/{id}/transactions")
    public List<CollectivityTransactionDTO> getTransactions(@PathVariable String id, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return collectivityService.getTransactions(id, from, to);
    }

    @GetMapping("/{id}")
    public CollectivityDTO getCollectivityById(@PathVariable String id) {
        return collectivityService.getCollectivityById(id);
    }

    @GetMapping("/{id}/financialAccounts")
    public List<FinancialAccountDTO> getFinancialAccounts(@PathVariable String id, @RequestParam LocalDate at) {
        return collectivityService.getFinancialAccounts(id, at);
    }

    @GetMapping("/{id}/statistics")
    public List<CollectivityLocalStatisticsDTO> getMemberStatistics(@PathVariable String id, @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return collectivityService.getMemberStatistics(id, from, to);
    }

    @GetMapping("/statistics")
    public List<CollectivityOverallStatisticsDTO> getAllCollectivitiesStatistics(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return collectivityService.getAllCollectivitiesStatistics(from, to);
    }
}