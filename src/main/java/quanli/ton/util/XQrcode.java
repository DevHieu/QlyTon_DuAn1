package quanli.ton.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class XQrcode {

    public static void createQrcode(double money, String customerName) throws Exception {
        String bankBin = "970436";            // mã BIN ngân hàng (6 chữ số)
        String accountNumber = "1032153715"; // số tk hoặc số thẻ tuỳ dịch vụ
        String accountName = "BUI MINH HIEU";
        long amount = (long) money;                 // 0 => bỏ tag 54
        String addInfo = customerName + " Thanh toan hoa don";

        String payload = buildVietQR(bankBin, accountNumber, accountName, amount, addInfo);
        System.out.println("Payload EMVCo: " + payload);

        // Tạo QR image
        int width = 350, height = 350;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter()
                .encode(payload, BarcodeFormat.QR_CODE, width, height, hints);

        Path tempPath = Files.createTempFile("qrcode", ".png");
        MatrixToImageWriter.writeToPath(matrix, "PNG", tempPath);

// Dùng XIcon.copyTo để copy QR vào folder đích
        File finalFile = XIcon.copyTo(tempPath.toFile(), "images/qrcode", "qrcode");
        System.out.println("QR saved to: " + finalFile.getAbsolutePath());
    }

    private static String buildVietQR(String bankBin, String accountNumber, String accountName, long amount, String addInfo) {
        // 00 - payload version, 01 - POI method (12 = dynamic, 11 = static)
        String p00 = tlv("00", "01");
        String p01 = tlv("01", "12");

        // inside tag 38 (NAPAS): must be nested:
        // 38 => { 00: GUI(A000000727), 01: {00: bankBin, 01: accountNumber}, 02: serviceCode }
        String gui = tlv("00", "A000000727"); // GUI Napas
        String bankInner = tlv("00", bankBin) + tlv("01", accountNumber); // sub-template under '01'
        String bankTemplate = tlv("01", bankInner);
        String service = tlv("02", "QRIBFTTA"); // chuyển đến tài khoản. Nếu chuyển đến thẻ dùng "QRIBFTTC"

        String merchantAccountInfo = tlv("38", gui + bankTemplate + service);

        // other root fields
        String mcc = tlv("52", "0000");        // merchant category code (0000 default)
        String currency = tlv("53", "704");   // VND
        String amt = amount > 0 ? tlv("54", String.valueOf(amount)) : "";
        String country = tlv("58", "VN");
        String mName = tlv("59", accountName);
        String mCity = tlv("60", "HANOI");
        String additional = tlv("62", tlv("08", addInfo)); // ID 08 trong template 62 = description

        // ghép rồi thêm CRC placeholder '6304'
        String withoutCrc = p00 + p01 + merchantAccountInfo + mcc + currency + amt + country + mName + mCity + additional + "6304";
        String crc = calculateCRC16(withoutCrc);
        return withoutCrc + crc;
    }

    private static String tlv(String id, String value) {
        int len = value.getBytes(StandardCharsets.UTF_8).length;
        return id + String.format("%02d", len) + value;
    }

    private static String calculateCRC16(String data) {
        int crc = 0xFFFF;
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = ((crc << 1) ^ 0x1021) & 0xFFFF;
                } else {
                    crc = (crc << 1) & 0xFFFF;
                }
            }
        }
        return String.format("%04X", crc & 0xFFFF);
    }
}
