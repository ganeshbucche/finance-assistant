package com.example.finance_assistant.controller;

import com.example.finance_assistant.dto.MonthlySummaryResponse;
import com.example.finance_assistant.entity.User;
import com.example.finance_assistant.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public MonthlySummaryResponse monthly(@AuthenticationPrincipal User user,
                                          @RequestParam(required = false) String month) {
        return reportService.monthlySummary(user, month);
    }
}