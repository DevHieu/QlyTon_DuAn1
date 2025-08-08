package quanli.ton.util;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;

import java.io.FileOutputStream;
import java.util.List;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.dao.ProductTypeDAO;
import quanli.ton.dao.impl.ProductsDAOimpl;
import quanli.ton.dao.impl.ProductTypeDAOImpl;
import quanli.ton.entity.BillDetails;
import quanli.ton.entity.Bills;

/**
 * Utility class để xuất dữ liệu ra file PDF (iTextPDF 8.0.0)
 */
public class XPdf {

    private static String title = "";
    private static String[] headers = {};
    private static List<Object[]> data = null;

    /**
     * Thiết lập tiêu đề cho PDF
     */
    public static void setTitle(String title) {
        XPdf.title = title;
    }

    /**
     * Thiết lập headers cho bảng
     */
    public static void setHeaders(String[] headers) {
        XPdf.headers = headers;
    }

    /**
     * Thiết lập dữ liệu cho bảng
     */
    public static void setData(List<Object[]> data) {
        XPdf.data = data;
    }

    /**
     * Tạo phiếu giao hàng
     */
    public static void createBillNote(String filePath, Bills bill,
            List<BillDetails> billDetails,
            quanli.ton.entity.Customer customer) throws Exception {

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // Tiêu đề công ty
        PdfFont companyFont = createVietnameseFont();
        Paragraph companyTitle = new Paragraph("NHÀ MÁY TÔN HOA MAI")
                .setFont(companyFont)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT);
        document.add(companyTitle);

        // Thông tin công ty
        PdfFont infoFont = createVietnameseFont();
        Paragraph companyInfo = new Paragraph("Địa chỉ: Phòng T1015, QTSC 9 Building, Đ. Tô Ký, Tân Chánh Hiệp, Quận 12, Hồ Chí Minh, Việt Nam)\n"
                + "SĐT: 0912 873 456 - 0964 273 612")
                .setFont(infoFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT);
        document.add(companyInfo);

        // Serial và tiêu đề phiếu
        Paragraph serialParagraph = new Paragraph("Serial: " + bill.getId())
                .setFont(infoFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(serialParagraph);

        PdfFont titleFont = createVietnameseFont();
        Paragraph deliveryTitle = new Paragraph("PHIẾU GIAO HÀNG")
                .setFont(titleFont)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(deliveryTitle);

        String billNumber = String.format("Số phiếu: %06d/%02d/%04d", bill.getId(),
                XDate.parse(XDate.format(bill.getCheckin(), "dd-MM-yyyy"), "dd-MM-yyyy").getMonth() + 1,
                XDate.parse(XDate.format(bill.getCheckin(), "dd-MM-yyyy"), "dd-MM-yyyy").getYear() + 1900);
        String billDate = "Ngày lập đơn: " + XDate.format(bill.getCheckin(), "dd/MM/yyyy");
        String customerInfo = "Khách hàng: " + customer.getFullName() + " - " + customer.getPhoneNumber();

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth();

        Cell leftCell = new Cell().add(new Paragraph(billNumber).setFont(infoFont).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);

        Cell rightCell = new Cell().add(new Paragraph(billDate).setFont(infoFont).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph(customerInfo).setFont(infoFont).setFontSize(10));

        // Bảng hàng hóa
        float[] columnWidths = {30, 180, 50, 50, 50, 80, 100};
        Table table = new Table(UnitValue.createPointArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        PdfFont headerFont = createVietnameseFont();
        table.addCell(createCell("STT", headerFont, true, 10));
        table.addCell(createCell("Tên hàng hóa", headerFont, true, 10));
        table.addCell(createCell("ĐVT", headerFont, true, 10));
        table.addCell(createCell("Chiều dài", headerFont, true, 10));
        table.addCell(createCell("S/ Lượng", headerFont, true, 10));
        table.addCell(createCell("Đơn giá", headerFont, true, 10));
        table.addCell(createCell("Thành tiền", headerFont, true, 10));

        PdfFont dataFont = createVietnameseFont();
        double totalAmount = 0;

        for (int i = 0; i < billDetails.size(); i++) {
            quanli.ton.entity.BillDetails detail = billDetails.get(i);
            quanli.ton.entity.Product product = getProductById(detail.getProductId());

            table.addCell(createCell(String.valueOf(i + 1), dataFont, false, 9));

            String productName = (product != null) ? product.getName() : "Sản phẩm không xác định";
            String productUnit = (product != null) ? getProductUnit(product.getTypeId()) : "cái";

            table.addCell(createCell(productName, dataFont, false, 9));
            table.addCell(createCell(productUnit, dataFont, false, 9));

            String lengthStr = (detail.getLength() == 0) ? "-" : String.format("%.2f", detail.getLength());
            table.addCell(createCell(lengthStr, dataFont, false, 9));
            table.addCell(createCell(String.valueOf((int) detail.getQuantity()), dataFont, false, 9));
            table.addCell(
                    createCell(String.format("%,.0f VNĐ", detail.getUnitPrice()), dataFont, false, 9)
                            .setTextAlignment(TextAlignment.RIGHT) // Căn phải
            );

            double price;

            if (detail.getLength() == 0) {
                // Trường hợp không có chiều dài → tính theo cây
                price = detail.getUnitPrice();
            } else if (detail.getDefaultLength() == null) {
                // Không có defaultLength → tính theo mét
                price = detail.getUnitPrice() * detail.getLength();
            } else {
                // Có chiều dài và defaultLength → tính theo tỉ lệ mét
                price = (detail.getUnitPrice() / detail.getDefaultLength()) * detail.getLength();
            }

            // Tính tiền sau chiết khấu
            double discountedPrice = price - (price * detail.getDiscount() / 100);
            double itemTotal = discountedPrice * detail.getQuantity();

            totalAmount += itemTotal;
            table.addCell(
                    createCell(String.format("%,.0f VNĐ", itemTotal), dataFont, false, 9)
                            .setTextAlignment(TextAlignment.RIGHT) // Căn phải
            );
        }

        // Tổng cộng
        Cell totalLabel = new Cell(1, 5)
                .add(new Paragraph("TỔNG CỘNG:"))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalLabel);

        Cell totalValue = new Cell(1, 2)
                .add(new Paragraph(String.format("%,.0f VNĐ", totalAmount)))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalValue);

        Cell discountLabel = new Cell(1, 5)
                .add(new Paragraph("GIẢM GIÁ:"))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(discountLabel);

        double discount = totalAmount / 100 * bill.getDiscount();
        Cell discountValue = new Cell(1, 2)
                .add(new Paragraph(String.format("%,.0f VNĐ", discount)))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(discountValue);

        Cell depositLabel = new Cell(1, 5)
                .add(new Paragraph("ĐẶT CỌC:"))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(depositLabel);

        Cell depositValue = new Cell(1, 2)
                .add(new Paragraph(String.format("%,.0f VNĐ", bill.getDeposit())))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(depositValue);

        Cell remainingLabel = new Cell(1, 5)
                .add(new Paragraph("CÒN LẠI PHẢI TRẢ:"))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(remainingLabel);

        double remaining = totalAmount - discount - bill.getDeposit();
        Cell remainingValue = new Cell(1, 2)
                .add(new Paragraph(String.format("%,.0f VNĐ", remaining)))
                .setFont(headerFont)
                .setFontSize(10)
                .setBold()
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(remainingValue);
        document.add(table);

        // Bằng chữ - canh phải
        PdfFont italicFont = createVietnameseFont();
        String amountInWords = convertToVietnameseWords((long) remaining);
        document.add(new Paragraph("(Bằng chữ: " + amountInWords + ")")
                .setFont(italicFont)
                .setFontSize(9)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT));

        // Ghi chú của đơn hàng
        document.add(new Paragraph("Ghi chú: " + bill.getNote())
                .setFont(italicFont)
                .setFontSize(10))
                .setItalic();

        // Lập phiếu ↔ Ký tên
        Table signTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth();

// Người lập phiếu
        Paragraph signer1 = new Paragraph()
                .add("Người lập phiếu\n")
                .add(new Text("(Ký và ghi rõ họ tên)").setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(infoFont)
                .setFontSize(10);

        Cell cell1 = new Cell()
                .add(signer1)
                .setBorder(Border.NO_BORDER);

// Người nhận hàng
        Paragraph signer2 = new Paragraph()
                .add("Người nhận hàng\n")
                .add(new Text("(Ký và ghi rõ họ tên)").setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(infoFont)
                .setFontSize(10);

        Cell cell2 = new Cell()
                .add(signer2)
                .setBorder(Border.NO_BORDER);

        signTable.addCell(cell1);
        signTable.addCell(cell2);

        document.add(signTable);

        document.close();
    }

    /**
     * Tạo cell cho bảng
     */
    private static Cell createCell(String text, PdfFont font, boolean isBold, int fontSize) {
        Cell cell = new Cell().add(new Paragraph(text))
                .setFont(font)
                .setFontSize(fontSize)
                .setBorder(new SolidBorder(1))
                .setPadding(3)
                .setTextAlignment(TextAlignment.CENTER);

        if (isBold) {
            cell.setBold();
        }

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
        if (number == 0) {
            return "không đồng";
        }

        String[] units = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};
        String[] numbers = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};

        if (number < 1000) {
            return convertLessThanOneThousand((int) number, numbers) + " đồng";
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
        if (number == 0) {
            return "";
        }

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
    private static PdfFont createVietnameseFont() {
        try {
            // Load font từ resources - cần file font hỗ trợ Unicode
            String fontPath = "src/main/resources/fonts/SVN-Arial.ttf";
            return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
        } catch (Exception e) {
            try {
                // Fallback về Helvetica với Unicode support
                return PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.IDENTITY_H);
            } catch (Exception ex) {
                try {
                    // Last fallback
                    return PdfFontFactory.createFont(StandardFonts.HELVETICA);
                } catch (Exception exx) {
                    throw new RuntimeException("Không thể tạo font", exx);
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
