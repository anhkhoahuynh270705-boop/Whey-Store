package com.gymstore.report.mapper;

import org.mapstruct.Mapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for JasperReports parameters using MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ReportParamMapper {

    default Map<String, Object> revenueParams(LocalDate from, LocalDate to, String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("DATE_FROM", Date.valueOf(from));
        params.put("DATE_TO", Date.valueOf(to));
        params.put("REPORT_TITLE", title != null ? title
                : "Báo cáo doanh thu từ " + from + " đến " + to);
        return params;
    }

    default Map<String, Object> invoiceParams(Long orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", orderId);
        return params;
    }

    default Map<String, Object> topProductsParams(LocalDate from, LocalDate to, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("DATE_FROM", Date.valueOf(from));
        params.put("DATE_TO", Date.valueOf(to));
        params.put("TOP_LIMIT", limit);
        return params;
    }

    default Map<String, Object> emptyParams() {
        return new HashMap<>();
    }
}
