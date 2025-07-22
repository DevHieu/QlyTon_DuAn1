/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import quanli.ton.ui.QlyTonJFrame;

/**
 *
 * @author hieud
 */
public class SpinnerEditor extends DefaultCellEditor {

    private JSpinner input;
    private QlyTonJFrame mainFrame;
    private JTable table;
    private boolean isUpdating = false; // Tránh lặp liên tục

    public SpinnerEditor(QlyTonJFrame mainFrame) {
        super(new JCheckBox());
        input = new JSpinner();
        this.mainFrame = mainFrame;
        SpinnerNumberModel numberModel = (SpinnerNumberModel) input.getModel();
        numberModel.setMinimum(1);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
        formatter.setCommitsOnValidEdit(true);
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);

        input.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating && table != null && mainFrame != null) {
                    int row = table.getEditingRow();
                    if (row >= 0) {
                        int newValue = (Integer) input.getValue();

                        // Update ngay lập tức khi user thay đổi spinner
                        SwingUtilities.invokeLater(() -> {
                            mainFrame.updateBillDetailQuantity(row, newValue);
                        });
                    }
                }
            }
        });

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        this.table = table;
        int qty = Integer.parseInt(value.toString());
        input.setValue(qty);
        return input;
    }

    @Override
    public Object getCellEditorValue() {
        return input.getValue();
    }

}
