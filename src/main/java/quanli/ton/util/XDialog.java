package quanli.ton.util;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class XDialog {

    // Thông báo nhẹ nhàng
    public static void notify(String message) {
        notify(message, "Thông báo");
    }

    public static void notify(String message, String title) {
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        UIManager.put("OptionPane.okButtonText", "OK"); // Reset
    }

    // Cảnh báo (kiểu màu vàng)
    public static void alert(String message) {
        alert(message, "Cảnh báo!");
    }

    public static void alert(String message, String title) {
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
        UIManager.put("OptionPane.okButtonText", "OK"); // Reset
    }

    // Báo lỗi (màu đỏ)
    public static void error(String message) {
        error(message, "Lỗi!");
    }

    public static void error(String message, String title) {
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        UIManager.put("OptionPane.okButtonText", "OK");
    }

    // Xác nhận (Có/Không)
    public static boolean confirm(String message) {
        return confirm(message, "Xác nhận!");
    }

    public static boolean confirm(String message, String title) {
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        return result == JOptionPane.YES_OPTION;
    }

    // Nhập liệu
    public static String prompt(String message) {
        return prompt(message, "Nhập vào!", null);
    }

    public static String prompt(String message, String defaultValue) {
        return prompt(message, "Nhập vào!", defaultValue);
    }

    public static String prompt(String message, String title, String defaultValue) {
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        UIManager.put("OptionPane.cancelButtonText", "Hủy");
        String result = (String) JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                defaultValue
        );
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        return result;
    }
}
