/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import quanli.ton.ui.QlyTonJFrame;

public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private JButton button = new JButton("Xoá");
    private JTable table;
    private QlyTonJFrame mainFrame;

    public ButtonEditor(QlyTonJFrame mainFrame) {
        this.mainFrame = mainFrame;

        button.addActionListener((ActionEvent e) -> {
            int row = table.getEditingRow();
            if (row >= 0) {

                // Nếu đang edit chính dòng đó thì stopCellEditing trước
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }

                mainFrame.deleteBillDetailColumn(row);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
