package quanli.ton.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Date;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.dao.ProductTypeDAO;
import quanli.ton.dao.impl.ProductsDAOimpl;
import quanli.ton.dao.impl.ProductTypeDAOImpl;

/**
 * Utility class để xuất dữ liệu ra file PDF
 */
public class XPDF {
    
    private static String title = "";
    private static String[] headers = {};
    private static List<Object[]> data = null;
    
    /**
     * Thiết lập tiêu đề cho PDF
     */
    public static void setTitle(String title) {
        XPDF.title = title;
    }
    
    /**
     * Thiết lập headers cho bảng
     */
    public static void setHeaders(String[] headers) {
        XPDF.headers = headers;
    }
    
    /**
     * Thiết lập dữ liệu cho bảng
     */
    public static void setData(List<Object[]> data) {
        XPDF.data = data;
    }
    
    /**
     * Tạo file PDF
     */
    public static void createPDF(String filePath) throws Exception {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Thêm tiêu đề
        if (!title.isEmpty()) {
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20);
            document.add(titleParagraph);
        }
        
        // Tạo bảng
        if (data != null && !data.isEmpty()) {
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            
            // Thêm headers
            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // Thêm dữ liệu
            Font dataFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            for (Object[] row : data) {
                for (Object cell : row) {
                    String cellValue = cell != null ? cell.toString() : "";
                    PdfPCell pdfCell = new PdfPCell(new Phrase(cellValue, dataFont));
                    pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfCell.setPadding(3);
                    table.addCell(pdfCell);
                }
            }
            
            document.add(table);
        }
        
        // Thêm thông tin cuối trang
        Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);
        Paragraph footer = new Paragraph("Được tạo vào: " + XDate.format(new java.util.Date(), "dd-MM-yyyy HH:mm:ss"), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
        
        document.close();
    }
    
    /**
     * Tạo PDF cho báo cáo doanh thu
     */
    public static void createRevenuePDF(String filePath, List<quanli.ton.entity.Revenue.ByCategory> revenueData) throws Exception {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Tiêu đề
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph titleParagraph = new Paragraph("BÁO CÁO DOANH THU THEO DANH MỤC", titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(20);
        document.add(titleParagraph);
        
        // Tạo bảng
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        // Headers
        String[] headers = {"Danh mục", "Doanh thu", "Số lượng", "Giá min", "Giá max", "Giá TB"};
        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Dữ liệu
        Font dataFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        double totalRevenue = 0;
        long totalQuantity = 0;
        
        for (quanli.ton.entity.Revenue.ByCategory item : revenueData) {
            table.addCell(new PdfPCell(new Phrase(item.getCategory(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%,.0f", item.getRevenue()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%,.0f", item.getMinPrice()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%,.0f", item.getMaxPrice()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%,.0f", item.getAvgPrice()), dataFont)));
            
            totalRevenue += item.getRevenue();
            totalQuantity += item.getQuantity();
        }
        
        document.add(table);
        
        // Tổng cộng
        Font totalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Paragraph totalParagraph = new Paragraph();
        totalParagraph.add(new Chunk("Tổng doanh thu: ", totalFont));
        totalParagraph.add(new Chunk(String.format("%,.0f VNĐ", totalRevenue), totalFont));
        totalParagraph.add(new Chunk(" | Tổng số lượng: ", totalFont));
        totalParagraph.add(new Chunk(String.valueOf(totalQuantity), totalFont));
        totalParagraph.setSpacingBefore(20);
        document.add(totalParagraph);
        
        // Footer
        Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);
        Paragraph footer = new Paragraph("Được tạo vào: " + XDate.format(new java.util.Date(), "dd-MM-yyyy HH:mm:ss"), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
        
        document.close();
    }
    
    /**
     * Tạo PDF cho báo cáo kho hàng
     */
    public static void createStockPDF(String filePath, List<quanli.ton.entity.Product> products) throws Exception {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Tiêu đề
        Font titleFont = createVietnameseFont(18, Font.BOLD);
        Paragraph titleParagraph = new Paragraph("BÁO CÁO TÌNH TRẠNG KHO HÀNG", titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(20);
        document.add(titleParagraph);
        
        // Tạo bảng
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        
        // Headers
        String[] headers = {"Tên sản phẩm", "Số lượng", "Giá bán", "Trạng thái"};
        Font headerFont = createVietnameseFont(12, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Dữ liệu
        Font dataFont = createVietnameseFont(10, Font.NORMAL);
        int outOfStock = 0;
        int lowStock = 0;
        int normalStock = 0;
        
        for (quanli.ton.entity.Product product : products) {
            table.addCell(new PdfPCell(new Phrase(product.getName(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(product.getQuantity()), dataFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("%,.0f", product.getUnitPrice()), dataFont)));
            
            String status;
            Font statusFont;
            if (product.getQuantity() == 0) {
                status = "HẾT HÀNG";
                statusFont = createVietnameseFont(10, Font.BOLD);
                statusFont.setColor(BaseColor.RED);
                outOfStock++;
            } else if (product.getQuantity() <= 100) {
                status = "SẮP HẾT";
                statusFont = createVietnameseFont(10, Font.BOLD);
                statusFont.setColor(BaseColor.ORANGE);
                lowStock++;
            } else {
                status = "ĐỦ HÀNG";
                statusFont = createVietnameseFont(10, Font.NORMAL);
                normalStock++;
            }
            
            PdfPCell statusCell = new PdfPCell(new Phrase(status, statusFont));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(statusCell);
        }
        
        document.add(table);
        
        // Thống kê tổng quan
        Font statTitleFont = createVietnameseFont(14, Font.BOLD);
        Paragraph statTitleParagraph = new Paragraph("THỐNG KÊ TỔNG QUAN:", statTitleFont);
        statTitleParagraph.setSpacingBefore(20);
        statTitleParagraph.setSpacingAfter(10);
        document.add(statTitleParagraph);
        
        // Chi tiết thống kêx
        Font statFont = createVietnameseFont(12, Font.BOLD);
        Paragraph statParagraph = new Paragraph();
        statParagraph.add(new Chunk("• Tổng số sản phẩm: " + products.size() + "\n", statFont));
        statParagraph.add(new Chunk("• Sản phẩm hết hàng: " + outOfStock + "\n", statFont));
        statParagraph.add(new Chunk("• Sản phẩm sắp hết: " + lowStock + "\n", statFont));
        statParagraph.add(new Chunk("• Sản phẩm đủ hàng: " + normalStock, statFont));
        statParagraph.setSpacingBefore(10);
        document.add(statParagraph);
        
        // Footer
        Font footerFont = createVietnameseFont(8, Font.ITALIC);
        Paragraph footer = new Paragraph("Được tạo vào: " + XDate.format(new java.util.Date(), "dd-MM-yyyy HH:mm:ss"), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
        
        document.close();
    }
    
    /**
     * Tạo phiếu giao hàng
     */
    public static void createDeliveryNote(String filePath, quanli.ton.entity.Bills bill, 
                                        List<quanli.ton.entity.BillDetails> billDetails,
                                        quanli.ton.entity.Customer customer) throws Exception {
        
        // Debug: In ra thông tin để kiểm tra
        System.out.println("=== DEBUG PHIEU GIAO HANG ===");
        System.out.println("Bill ID: " + bill.getId());
        System.out.println("Customer: " + customer.getFullName());
        System.out.println("Bill Details count: " + billDetails.size());
        
        for (int i = 0; i < billDetails.size(); i++) {
            quanli.ton.entity.BillDetails detail = billDetails.get(i);
            System.out.println("Detail " + i + ": ProductID=" + detail.getProductId() + 
                             ", Quantity=" + detail.getQuantity() + 
                             ", UnitPrice=" + detail.getUnitPrice());
        }
        System.out.println("=== END DEBUG ===");
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Tiêu đề công ty
        Font companyFont = createVietnameseFont(14, Font.BOLD);
        Paragraph companyTitle = new Paragraph("NHÀ MÁY TÔN - SẮT THÉP", companyFont);
        companyTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(companyTitle);
        
        // Thông tin công ty
        Font infoFont = createVietnameseFont(10, Font.NORMAL);
        Paragraph companyInfo = new Paragraph("DNTN SẮT THÉP ĐỨC ĐỨC MINH\n" +
                                           "Ấp 1B, Phước Thái, LT - ĐN (đối diện bưu điện Phước Thái)\n" +
                                           "ĐT: 0933 341 307 - 0946 708 306", infoFont);
        companyInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(companyInfo);
        
        // Serial và tiêu đề phiếu
        Paragraph serialParagraph = new Paragraph("Serial: " + bill.getId(), infoFont);
        serialParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(serialParagraph);
        
        Font titleFont = createVietnameseFont(16, Font.BOLD);
        Paragraph deliveryTitle = new Paragraph("PHIẾU GIAO HÀNG", titleFont);
        deliveryTitle.setAlignment(Element.ALIGN_CENTER);
        deliveryTitle.setSpacingAfter(10);
        document.add(deliveryTitle);
        
        // Thông tin phiếu và khách hàng
        String billNumber = String.format("Số phiếu: %06d/%02d/%04d", bill.getId(), 
                                        XDate.parse(XDate.format(bill.getCheckin(), "dd-MM-yyyy"), "dd-MM-yyyy").getMonth() + 1,
                                        XDate.parse(XDate.format(bill.getCheckin(), "dd-MM-yyyy"), "dd-MM-yyyy").getYear() + 1900);
        String billDate = "Ngày: " + XDate.format(bill.getCheckin(), "dd/MM/yy");
        String customerInfo = "Khách hàng: " + customer.getFullName() + " - " + customer.getPhoneNumber();
        
        document.add(new Paragraph(billNumber + "    " + billDate, infoFont));
        document.add(new Paragraph(customerInfo, infoFont));
        
        // Bảng hàng hóa
        float[] columnWidths = {30, 200, 80, 80, 100};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        
        // Headers
        Font headerFont = createVietnameseFont(10, Font.BOLD);
        table.addCell(createCell("STT", headerFont, true));
        table.addCell(createCell("Tên hàng hóa", headerFont, true));
        table.addCell(createCell("S/ Lượng", headerFont, true));
        table.addCell(createCell("Đơn giá", headerFont, true));
        table.addCell(createCell("Thành tiền", headerFont, true));
        
        // Dữ liệu sản phẩm
        Font dataFont = createVietnameseFont(9, Font.NORMAL);
        double totalAmount = 0;
        
        for (int i = 0; i < billDetails.size(); i++) {
            quanli.ton.entity.BillDetails detail = billDetails.get(i);
            quanli.ton.entity.Product product = getProductById(detail.getProductId());
            
            table.addCell(createCell(String.valueOf(i + 1), dataFont, false));
            
            // Xử lý trường hợp product null
            String productName = (product != null) ? product.getName() : "Sản phẩm không xác định";
            String productUnit = (product != null) ? getProductUnit(product.getTypeId()) : "cái";
            
            table.addCell(createCell(productName, dataFont, false));
            table.addCell(createCell(detail.getQuantity() + " " + productUnit, dataFont, false));
            table.addCell(createCell(String.format("%,.0f", detail.getUnitPrice()), dataFont, false));
            
            double itemTotal = detail.getQuantity() * detail.getUnitPrice();
            totalAmount += itemTotal;
            table.addCell(createCell(String.format("%,.0f", itemTotal), dataFont, false));
        }
        
        // Tổng cộng
        PdfPCell totalLabel = createCell("TỔNG CỘNG", headerFont, true);
        totalLabel.setColspan(4);
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalLabel);
        
        PdfPCell totalValue = createCell(String.format("%,.0f", totalAmount), headerFont, true);
        table.addCell(totalValue);
        
        document.add(table);
        
        // Bằng chữ
        Font italicFont = createVietnameseFont(9, Font.ITALIC);
        String amountInWords = convertToVietnameseWords((long)totalAmount);
        document.add(new Paragraph("(Bằng chữ: " + amountInWords + ")", italicFont));
        
        // Ký tên
        document.add(new Paragraph("\nLập phiếu                                                 (Ký và ghi rõ họ tên)"));
        
        document.close();
    }
    
    /**
     * Tạo cell cho bảng
     */
    private static PdfPCell createCell(String text, Font font, boolean isBold) {
        if (isBold) {
            font.setStyle(Font.BOLD);
        }
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
    
    /**
     * Lấy thông tin sản phẩm theo ID
     */
    private static quanli.ton.entity.Product getProductById(String productId) {
        try {
            ProductsDAO productDao = new ProductsDAOimpl();
            quanli.ton.entity.Product product = productDao.findById(productId);
            if (product == null) {
                System.out.println("Không tìm thấy sản phẩm với ID: " + productId);
            }
            return product;
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy đơn vị sản phẩm
     */
    private static String getProductUnit(String typeId) {
        try {
            ProductTypeDAO typeDao = new ProductTypeDAOImpl();
            quanli.ton.entity.ProductType type = typeDao.findById(typeId);
            return type != null ? type.getUnit() : "cái";
        } catch (Exception e) {
            return "cái";
        }
    }
    
    /**
     * Chuyển số thành chữ tiếng Việt
     */
    private static String convertToVietnameseWords(long number) {
        if (number == 0) return "không đồng";
        
        String[] units = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};
        String[] numbers = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        
        if (number < 1000) {
            return convertLessThanOneThousand((int)number, numbers) + " đồng";
        }
        
        int unitIndex = 0;
        StringBuilder result = new StringBuilder();
        
        while (number > 0) {
            int group = (int) (number % 1000);
            if (group != 0) {
                String groupText = convertLessThanOneThousand(group, numbers);
                if (unitIndex > 0) {
                    groupText += " " + units[unitIndex];
                }
                if (result.length() > 0) {
                    result.insert(0, " ");
                }
                result.insert(0, groupText);
            }
            number /= 1000;
            unitIndex++;
        }
        
        return result.toString() + " đồng";
    }
    
    private static String convertLessThanOneThousand(int number, String[] numbers) {
        if (number == 0) return "";
        
        if (number < 10) {
            return numbers[number];
        }
        
        if (number < 20) {
            return "mười " + (number == 11 ? "một" : numbers[number - 10]);
        }
        
        if (number < 100) {
            String result = numbers[number / 10] + " mươi";
            if (number % 10 != 0) {
                result += " " + numbers[number % 10];
            }
            return result;
        }
        
        String result = numbers[number / 100] + " trăm";
        int remainder = number % 100;
        if (remainder != 0) {
            if (remainder < 10) {
                result += " linh " + numbers[remainder];
            } else {
                result += " " + convertLessThanOneThousand(remainder, numbers);
            }
        }
        return result;
    }
    
    /**
     * Tạo font hỗ trợ tiếng Việt
     */
    private static Font createVietnameseFont(int size, int style) {
        try {
            // Thử tạo font với encoding Unicode tốt hơn
            BaseFont baseFont = BaseFont.createFont("Helvetica", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            try {
                // Thử với encoding khác
                BaseFont baseFont2 = BaseFont.createFont("Helvetica", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                return new Font(baseFont2, size, style);
            } catch (Exception e2) {
                try {
                    // Thử với font Times New Roman
                    BaseFont baseFont3 = BaseFont.createFont("Times-Roman", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    return new Font(baseFont3, size, style);
                } catch (Exception e3) {
                    // Fallback về font thường
                    return new Font(Font.FontFamily.HELVETICA, size, style);
                }
            }
        }
    }
    
    /**
     * Clear dữ liệu
     */
    public static void clear() {
        title = "";
        headers = new String[0];
        data = null;
    }
} 