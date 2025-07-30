/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package quanli.ton.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import quanli.ton.controller.HistoryController;
import quanli.ton.dao.BillDao;
import quanli.ton.dao.impl.BillDaoImpl;
import quanli.ton.entity.Bills;
import quanli.ton.util.TimeRange;
import quanli.ton.util.XAuth;
import quanli.ton.util.XDate;

/**
 *
 * @author huynhtrunghieu
 */
    public class History extends javax.swing.JDialog implements HistoryController {
    private BillDao billDao; // Sử dụng interface
    private List<Bills> bills; // Danh sách hóa đơn cho hiển thị và chi tiết

    private void initTable() {
        // Cập nhật các cột hiển thị để khớp với dữ liệu từ selectByTimeRange
        String[] columns = {"Mã HĐ", "Nhân viên", "Khách hàng", "Ngày tạo", "Ngày thanh toán", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        jTable1.setModel(model);
        
    JTableHeader header = jTable1.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Font lớn hơn, đậm
    header.setBackground(new Color(0, 191, 191)); // Màu xanh ngọc lam nổi bật (DarkCyan)
    header.setOpaque(true); // Đảm bảo màu nền được hiển thị
    header.setBorder(BorderFactory.createEmptyBorder()); // Xóa viền header nếu có

        // Điều chỉnh chiều rộng cột (tùy chỉnh theo dữ liệu thực tế)
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50); // Mã HĐ
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100); // Nhân viên
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150); // Khách hàng
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(150); // Ngày tạo
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150); // Ngày thanh toán
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100); // Trạng thái

        // --- Tùy chỉnh Cell của bảng (hiệu ứng sọc, màu chọn và căn chỉnh) ---
        jTable1.setGridColor(new Color(220, 220, 220)); // Màu xám nhạt cho đường kẻ giữa các cell
        jTable1.setShowGrid(true); // Hiển thị đường kẻ
        jTable1.setRowHeight(35); // Tăng chiều cao hàng để dễ nhìn hơn và tạo không gian
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Font cho dữ liệu trong bảng

        // Màu nền và màu chữ khi chọn hàng
        jTable1.setSelectionBackground(new Color(0, 150, 150)); // Màu xanh ngọc lam đậm hơn khi chọn
        jTable1.setSelectionForeground(Color.WHITE); // Chữ trắng khi chọn

        // Custom Cell Renderer để tạo hiệu ứng sọc (alternating row colors) và căn chỉnh
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color LIGHT_TURQUOISE_STRIPE = new Color(224, 255, 255); // Rất nhạt, gần trắng (LightCyan)
            private final Color LIGHT_YELLOW_STRIPE = new Color(255, 255, 204); // Vàng nhạt (LightYellow)

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Căn giữa nội dung cột "Mã HĐ" (cột 0)
                if (column == 0) {
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT); // Các cột khác căn trái
                }
                
                // Nếu hàng đang được chọn, sử dụng màu chọn của bảng
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    // Hiệu ứng sọc: hàng chẵn màu xanh nhạt, hàng lẻ màu vàng nhạt
                    if (row % 2 == 0) {
                        c.setBackground(LIGHT_TURQUOISE_STRIPE);
                    } else {
                        c.setBackground(LIGHT_YELLOW_STRIPE);
                    }
                    c.setForeground(table.getForeground()); // Giữ màu chữ mặc định của bảng
                }
                return c;
            }
        });
} 
            
    @Override
    public void open() {
        // Đặt JFrame ở giữa màn hình
        this.setLocationRelativeTo(null);
        // Chọn "Hôm nay" và điền dữ liệu
        cboTimeRanges.setSelectedIndex(0);
        fillBills();
    }

    @Override
    public void fillBills() {
        Date beginDate = null;
        Date endDate = null;

        // Xác định khoảng thời gian dựa trên combobox hoặc input thủ công
        if (cboTimeRanges.getSelectedIndex() != -1) {
            TimeRange timeRange;
            switch (cboTimeRanges.getSelectedIndex()) {
                case 0: timeRange = TimeRange.today(); break;
                case 1: timeRange = TimeRange.thisWeek(); break;
                case 2: timeRange = TimeRange.thisMonth(); break;
                case 3: timeRange = TimeRange.thisQuarter(); break;
                case 4: timeRange = TimeRange.thisYear(); break;
                default: timeRange = TimeRange.today(); // Mặc định là hôm nay
            }
            beginDate = timeRange.getBegin();
            endDate = timeRange.getEnd();
        } else { // Xử lý khi nhập ngày thủ công
            beginDate = XDate.parse(txtBegin.getText(), "dd-MM-yyyy");
            endDate = XDate.parse(txtEnd.getText(), "dd-MM-yyyy");
        }

        // Tải hóa đơn từ DAO
        List<Object[]> fetchedBills = billDao.selectByTimeRange(beginDate, endDate);

        // Đổ dữ liệu vào bảng
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (Object[] row : fetchedBills) {
            model.addRow(row);
        }
    }

    @Override
    public void showBillJDialog() {
        int selectedRow = jTable1.getSelectedRow();
    } 

    @Override
    public void selectTimeRange() {
        // Khi chọn combobox thì cập nhật lại bảng
        fillBills();
    }

    /**
     * Creates new form History
     */
    public History() {
        initComponents();
        billDao = new BillDaoImpl(); // Khởi tạo BillDaoImpl
        initTable(); // Khởi tạo cấu trúc bảng
        this.open(); // Đặt vị trí và điền dữ liệu ban đầ
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cboTimeRanges = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Từ Ngày");

        jLabel2.setText("Đến Ngày");

        jButton1.setText("Lọc");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cboTimeRanges.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay ", "Tuần này ", "Tháng này ", "Quý này ", "Năm nay" }));
        cboTimeRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTimeRangesActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Hoá Đơn ", "Người Tạo", "Tên Khách Hàng", "Thời Điểm Tạo Phiếu", "Thời Điểm Thanh Toán", "Trạng Thái"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.fillBills();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
                 this.open();
    }//GEN-LAST:event_formWindowOpened

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
            this.showBillJDialog();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void cboTimeRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTimeRangesActionPerformed
        // TODO add your handling code here:
        this.selectTimeRange();
    }//GEN-LAST:event_cboTimeRangesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new History().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtEnd;
    // End of variables declaration//GEN-END:variables

}
