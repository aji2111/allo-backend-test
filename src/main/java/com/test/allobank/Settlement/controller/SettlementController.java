package com.test.allobank.Settlement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.test.allobank.Settlement.dto.response.SettlementResponse;
import com.test.allobank.Settlement.service.SettlementService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@Tag(
        name = "Settlement",
        description = "Settlement calculation APIs"
)
@RequiredArgsConstructor
public class SettlementController {
    
    private final SettlementService settlementService;

    @GetMapping("/{groupPublicId}/settlement")
    public SettlementResponse getSettlement(
            @PathVariable UUID groupPublicId
    ) {
        return settlementService.getSettlement(groupPublicId);
    }
}