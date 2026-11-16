package com.gymstore.report.service.impl;

import com.gymstore.report.mapper.ReportParamMapper;
import com.gymstore.report.service.IReportService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Map;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements IReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    DataSource dataSource;
    ReportParamMapper reportParamMapper;

    @Override
    public byte[] generateRevenue(LocalDate from, LocalDate to, String format) throws Exception {
        Map<String, Object> params = reportParamMapper.revenueParams(from, to, null);
        return generateReport("revenue", params, format);
    }

    @Override
    public byte[] generateInvoice(Long orderId) throws Exception {
        Map<String, Object> params = reportParamMapper.invoiceParams(orderId);
        return generateReport("invoice", params, "pdf");
    }

    @Override
    public byte[] generateInventory(String format) throws Exception {
        return generateReport("inventory", reportParamMapper.emptyParams(), format);
    }

    @Override
    public byte[] generateTopProducts(LocalDate from, LocalDate to, int limit, String format) throws Exception {
        Map<String, Object> params = reportParamMapper.topProductsParams(from, to, limit);
        return generateReport("top-products", params, format);
    }

    // ── Core ─────────────────────────────────────────────────────────────────
    private byte[] generateReport(String reportName, Map<String, Object> params, String format) throws Exception {
        log.info("Tạo báo cáo: {} format={}", reportName, format);
        String resourcePath = "/reports/" + reportName + ".jrxml";
        InputStream jrxmlStream = getClass().getResourceAsStream(resourcePath);
        if (jrxmlStream == null) {
            throw new IllegalArgumentException("Không tìm thấy báo cáo: " + resourcePath);
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
        JasperPrint jasperPrint;
        try (Connection conn = dataSource.getConnection()) {
            jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if ("xlsx".equalsIgnoreCase(format)) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            SimpleXlsxReportConfiguration cfg = new SimpleXlsxReportConfiguration();
            cfg.setOnePagePerSheet(false);
            cfg.setDetectCellType(true);
            exporter.setConfiguration(cfg);
            exporter.exportReport();
        } else {
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            SimplePdfExporterConfiguration cfg = new SimplePdfExporterConfiguration();
            cfg.setMetadataTitle("GymStore Report");
            exporter.setConfiguration(cfg);
            exporter.exportReport();
        }

        return out.toByteArray();
    }
}
