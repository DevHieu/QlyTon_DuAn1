/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package quanli.ton.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import quanli.ton.controller.HistoryController;
import quanli.ton.dao.BillDao;
import quanli.ton.dao.impl.BillDaoImpl;
import quanli.ton.entity.Bills;
import quanli.ton.ui.manager.RevenueManager;
import quanli.ton.util.TimeRange;
import quanli.ton.util.XDate;

/**
 *
 * @author hieud
 */
public class HistoryJDialog extends javax.swing.JDialog implements HistoryController{

    private BillDao billDao = new BillDaoImpl();; // Sử dụng interface
    private List<Bills> bills;
    
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
        
        // Set ngày hiện tại cho textfield
        Date today = new Date();
        txtBegin.setText(XDate.format(today, "dd-MM-yyyy HH:mm:ss"));
        txtEnd.setText(XDate.format(today, "dd-MM-yyyy HH:mm:ss"));
        
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
        // Cập nhật ngày trong textfield khi chọn combobox
        Date beginDate = null;
        Date endDate = null;
        
        switch (cboTimeRanges.getSelectedIndex()) {
            case 0: // Hôm nay
                beginDate = TimeRange.today().getBegin();
                endDate = TimeRange.today().getEnd();
                break;
            case 1: // Tuần này
                beginDate = TimeRange.thisWeek().getBegin();
                endDate = TimeRange.thisWeek().getEnd();
                break;
            case 2: // Tháng này
                beginDate = TimeRange.thisMonth().getBegin();
                endDate = TimeRange.thisMonth().getEnd();
                break;
            case 3: // Quý này
                beginDate = TimeRange.thisQuarter().getBegin();
                endDate = TimeRange.thisQuarter().getEnd();
                break;
            case 4: // Năm nay
                beginDate = TimeRange.thisYear().getBegin();
                endDate = TimeRange.thisYear().getEnd();
                break;
        }
        
        // Cập nhật textfield với ngày tương ứng
        if (beginDate != null && endDate != null) {
            txtBegin.setText(XDate.format(beginDate, "dd-MM-yyyy HH:mm:ss"));
            txtEnd.setText(XDate.format(endDate, "dd-MM-yyyy HH:mm:ss"));
        }
        
        // Cập nhật lại bảng
        fillBills();
    }
    /**
     * Creates new form HistoryJDialog
     */
    public HistoryJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.open();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cboTimeRanges = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(735, 666));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Từ ngày:");
        jLabel1.setToolTipText("");

        txtBegin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Đến ngày:");
        jLabel2.setToolTipText("");

        txtEnd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton1.setBackground(new java.awt.Color(0, 102, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Lọc");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cboTimeRanges.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("LỊCH SỬ BÁN HÀNG");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 741, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator2)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(238, 238, 238)
                                    .addComponent(jLabel11))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel2)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(64, 64, 64)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 34, Short.MAX_VALUE))
                        .addComponent(jScrollPane1))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboTimeRanges, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.fillBills();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cboTimeRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTimeRangesActionPerformed
        // TODO add your handling code here:
        this.selectTimeRange();
    }//GEN-LAST:event_cboTimeRangesActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.showBillJDialog();
        }
    }//GEN-LAST:event_jTable1MouseClicked

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
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf()); // Dùng thư viện FlatLaf
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoryJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HistoryJDialog dialog = new HistoryJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtEnd;
    // End of variables declaration//GEN-END:variables
}
