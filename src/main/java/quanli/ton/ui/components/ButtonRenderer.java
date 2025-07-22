/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setText("Xoá");
        setBackground(new Color(244, 67, 54));  // Đỏ
        setForeground(Color.WHITE);             // Chữ trắng
        setFont(new Font("Segoe UI", Font.BOLD, 12)); // Font đậm đẹp
        setFocusPainted(false);                // Bỏ viền xanh khi focus
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Padding
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
