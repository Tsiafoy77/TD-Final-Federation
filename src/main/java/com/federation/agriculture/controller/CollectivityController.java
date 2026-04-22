package com.federation.agriculture.controller;

import com.federation.agriculture.dto.CreateCollectivityDTO;
import com.federation.agriculture.dto.CollectivityDTO;
import com.federation.agriculture.dto.CollectivityIdentityDTO;
import com.federation.agriculture.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CollectivityDTO> createCollectivities(@RequestBody List<CreateCollectivityDTO> requestBody) {
        return collectivityService.createCollectivities(requestBody);
    }

    @PutMapping("/{id}/identity")
    public CollectivityDTO assignIdentity(
            @PathVariable String id,
            @RequestBody CollectivityIdentityDTO identity) {
        return collectivityService.assignIdentity(id, identity);
    }
}