package com.gymstore.report.controller;

import com.gymstore.report.service.IReportService;
import com.gymstore.common.security.JwtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    /** Hóa đơn PDF – CUSTOMER xem của mình, STAFF/ADMIN xem tất cả. */
    @GetMapping("/invoice/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> invoice(@PathVariable Long orderId) throws Exception {
        byte[] data = reportService.generateInvoice(orderId);
        return pdfResponse(data, "invoice-" + orderId + ".pdf");
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "pdf") String format) throws Exception {
        byte[] data = reportService.generateRevenue(from, to, format);
        return fileResponse(data, "revenue-" + from + "-" + to + "." + format, format);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<byte[]> inventory(
            @RequestParam(defaultValue = "pdf") String format) throws Exception {
        byte[] data = reportService.generateInventory(format);
        return fileResponse(data, "inventory." + format, format);
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> topProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "pdf") String format) throws Exception {
        byte[] data = reportService.generateTopProducts(from, to, limit, format);
        return fileResponse(data, "top-products." + format, format);
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }

    private ResponseEntity<byte[]> fileResponse(byte[] data, String filename, String format) {
        MediaType mt = "xlsx".equalsIgnoreCase(format)
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_PDF;
        return ResponseEntity.ok()
                .contentType(mt)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }
}
