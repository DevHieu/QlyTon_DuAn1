package quanli.ton.ui.components;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.EventObject;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import quanli.ton.ui.QlyTonJFrame;

/**
 * Fixed SpinnerEditor - extends AbstractCellEditor instead of DefaultCellEditor
 * @author hieud
 */
public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    private JSpinner input;
    private QlyTonJFrame mainFrame;
    private JTable table;
    private boolean isUpdating = false;
    private int currentRow = -1;
    private Timer updateTimer; // Debounce updates

    public SpinnerEditor(QlyTonJFrame mainFrame) {
        this.mainFrame = mainFrame;
        input = new JSpinner();
        
        // Setup spinner model
        SpinnerNumberModel numberModel = (SpinnerNumberModel) input.getModel();
        numberModel.setMinimum(1);
        
        // Setup editor formatting
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
        formatter.setCommitsOnValidEdit(true);
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        
        // Timer để debounce updates (tránh quá nhiều calls)
        updateTimer = new Timer(300, e -> {
            if (!isUpdating && table != null && mainFrame != null && currentRow >= 0) {
                int newValue = (Integer) input.getValue();
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    try {
                        mainFrame.updateBillDetailQuantity(currentRow, newValue);
                    } finally {
                        isUpdating = false;
                    }
                });
            }
        });
        updateTimer.setRepeats(false);
        
        // === FOCUS LISTENERS ===
        
        // 1. Spinner focus listener
        input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!e.isTemporary() && !isChildComponent(e.getOppositeComponent())) {
                    stopCellEditing();
                }
            }
        });
        
        // 2. TextField focus listener (QUAN TRỌNG!)
        JTextField textField = editor.getTextField();
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!e.isTemporary() && !isChildComponent(e.getOppositeComponent())) {
                    stopCellEditing();
                }
            }
        });
        
        // === KEY LISTENERS ===
        
        // Enter key commits
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    stopCellEditing();
                }
                // Tab key commits
                else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    stopCellEditing();
                }
            }
        });
        
        // === CHANGE LISTENER ===
        
        input.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating && currentRow >= 0) {
                    // Restart timer để debounce
                    updateTimer.restart();
                }
            }
        });
    }
    
    /**
     * Kiểm tra xem component có phải là con của spinner không
     */
    private boolean isChildComponent(Component comp) {
        if (comp == null) return false;
        return SwingUtilities.isDescendingFrom(comp, input);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
            boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;
        
        try {
            int qty = (value != null) ? Integer.parseInt(value.toString()) : 1;
            isUpdating = true;
            input.setValue(qty);
        } catch (NumberFormatException e) {
            input.setValue(1);
        } finally {
            isUpdating = false;
        }
        
        // Request focus cho textfield
        SwingUtilities.invokeLater(() -> {
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
            editor.getTextField().requestFocus();
            editor.getTextField().selectAll();
        });
        
        return input;
    }

    @Override
    public Object getCellEditorValue() {
        Object value = input.getValue();
        return value;
    }

    @Override
    public boolean stopCellEditing() {
        
        // Stop timer nếu đang chạy
        if (updateTimer.isRunning()) {
            updateTimer.stop();
        }
        
        try {
            input.commitEdit();
        } catch (ParseException e) {
            // Không return false, vẫn cho phép stop editing
        }
        
        boolean result = super.stopCellEditing();
        currentRow = -1; // Reset
        return result;
    }

    @Override
    public void cancelCellEditing() {
        if (updateTimer.isRunning()) {
            updateTimer.stop();
        }
        currentRow = -1;
        super.cancelCellEditing();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }
}