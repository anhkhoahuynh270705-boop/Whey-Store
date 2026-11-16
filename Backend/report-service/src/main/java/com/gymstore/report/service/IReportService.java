package com.gymstore.report.service;

import java.time.LocalDate;

/**
 * Interface cho report service.
 */
public interface IReportService {

    /**
     * Xuất báo cáo doanh thu theo khoảng ngày.
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @param format "pdf" hoặc "xlsx"
     */
    byte[] generateRevenue(LocalDate from, LocalDate to, String format) throws Exception;

    /**
     * Xuất hóa đơn cho 1 đơn hàng.
     */
    byte[] generateInvoice(Long orderId) throws Exception;

    /**
     * Xuất báo cáo tồn kho hiện tại.
     */
    byte[] generateInventory(String format) throws Exception;

    /**
     * Xuất top sản phẩm bán chạy.
     */
    byte[] generateTopProducts(LocalDate from, LocalDate to, int limit, String format) throws Exception;
}
