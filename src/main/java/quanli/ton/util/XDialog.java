package quanli.ton.util;

import javax.swing.JOptionPane;

public class XDialog {

    public static void alert(String message) {
        XDialog.alert(message, "Thông báo!");
    }

    public static void alert(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(String message) {
        return XDialog.confirm(message, "Xác nhận!");
    }

    public static boolean confirm(String message, String title) {
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return (result == JOptionPane.YES_OPTION);
    }

    public static String prompt(String message) {
        return XDialog.prompt(message, "Nhập vào!", null);
    }

    public static String prompt(String message, String defaultValue) {
        return XDialog.prompt(message, "Nhập vào!", defaultValue);
    }

    public static String prompt(String message, String title, String defaultValue) {
        return (String) JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                defaultValue
        );
    }
}
