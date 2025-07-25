/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package quanli.ton.ui.manager;

import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import quanli.ton.controller.RevenueController;
import quanli.ton.dao.RevenueDAO;
import quanli.ton.dao.impl.RevenueDAOImpl;
import quanli.ton.entity.Revenue;
import quanli.ton.util.TimeRange;
import quanli.ton.util.XDate;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Collections;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Admin
 */
public class RevenueManager extends javax.swing.JDialog implements RevenueController {

    private javax.swing.JPanel jPanelChart;

    /**
     * Creates new form RevenueManager
     */
    public RevenueManager(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        open();
    }
    RevenueDAO dao = new RevenueDAOImpl();

    @Override
    public void open() {
        this.setLocationRelativeTo(null);
        this.selectTimeRange();
    }

    @Override
    public void selectTimeRange() {
        TimeRange range = TimeRange.today();
        switch (cboTimeRanges.getSelectedIndex()) {
            case 0 ->
                range = TimeRange.today();
            case 1 ->
                range = TimeRange.thisWeek();
            case 2 ->
                range = TimeRange.thisMonth();
            case 3 ->
                range = TimeRange.thisQuarter();
            case 4 ->
                range = TimeRange.thisYear();
        }
        txtBegin.setText(XDate.format(range.getBegin(), "MM/dd/yyyy"));
        txtEnd.setText(XDate.format(range.getEnd(), "MM/dd/yyyy"));

        this.fillRevenue();
    }

    @Override
    public void fillRevenue() {
        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");
        switch (tabs.getSelectedIndex()) {
            case 0 ->
                this.fillRevenueByCategory(begin, end);
            case 1 ->
                this.fillRevenueByUser(begin, end);
        }
    }

    private void addDonutChart(List<Revenue.ByCategory> list) {
        ChartPanel chartPanel = createDonutChartPanel(list);
        donutChart.removeAll();                   // xoá sạch bên trong
        donutChart.setLayout(new BorderLayout()); // đảm bảo layout
        donutChart.add(chartPanel, BorderLayout.CENTER); // add vào giữa
        donutChart.revalidate();
        donutChart.repaint();
    }

    private ChartPanel createDonutChartPanel(List<Revenue.ByCategory> items) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        items.forEach(item -> dataset.setValue(item.getCategory(), item.getQuantity()));


        JFreeChart chart = ChartFactory.createPieChart(
                "Số lượng bán ra", dataset, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

        return new ChartPanel(chart);
    }

    private void fillRevenueByCategory(Date begin, Date end) {
        List<Revenue.ByCategory> items = dao.getByCategory(begin, end);
        DecimalFormat moneyFormat = new DecimalFormat("#,##0 VNĐ");
        DefaultTableModel model = (DefaultTableModel) tblByCategory.getModel();
        model.setRowCount(0);

        // Kiểm tra và log dữ liệu
        System.out.println("Số lượng dữ liệu từ dao.getByCategory: " + (items != null ? items.size() : "null"));
        if (items == null || items.isEmpty()) {
            System.out.println("Không có dữ liệu để hiển thị bảng hoặc biểu đồ!");
        } else {
            items.forEach(item -> {
                Object[] row = {
                    item.getCategory(),
                    moneyFormat.format(item.getRevenue()),
                    item.getQuantity(), // nếu muốn format, dùng: numberFormat.format(item.getQuantity())
                    moneyFormat.format(item.getMinPrice()),
                    moneyFormat.format(item.getMaxPrice()),
                    moneyFormat.format(item.getAvgPrice())
                };
                model.addRow(row);
                System.out.println("Đã thêm hàng: " + item.getCategory() + ", Doanh thu: " + item.getRevenue());
            });
        }
        this.addDonutChart(items);
        double total = items.stream().mapToDouble(Revenue.ByCategory::getRevenue).sum();
        lblTotal.setText(moneyFormat.format(total));
    }

    
    private void fillRevenueByUser(Date begin, Date end) {
        List<Revenue.ByUser> items = dao.getByUser(begin, end);
        DecimalFormat moneyFormat = new DecimalFormat("#,##0 VNĐ");
        DefaultTableModel model = (DefaultTableModel) tblByUser.getModel();
        model.setRowCount(0);

        // 👉 Điền toàn bộ nhân viên vào bảng
        items.forEach(item -> {
            Object[] row = {
                item.getUser(),
                moneyFormat.format(item.getRevenue()),
                item.getQuantity(),
                XDate.format(item.getFirstTime(), "hh:mm:ss dd-MM-yyyy"),
                XDate.format(item.getLastTime(), "hh:mm:ss dd-MM-yyyy")
            };
            model.addRow(row);
        });

        // 👉 Chỉ chọn top 5 để vẽ biểu đồ
        Collections.sort(items, (o1, o2) -> Double.compare(o2.getRevenue(), o1.getRevenue()));
        List<Revenue.ByUser> top5Items = items.size() > 5 ? items.subList(0, 5) : items;
        addBarChart(top5Items);

        // 👉 Tổng doanh thu của tất cả nhân viên
        double total = items.stream().mapToDouble(Revenue.ByUser::getRevenue).sum();
        lblTotalAmountUser.setText(moneyFormat.format(total));
    }

    
    private void addBarChart(List<Revenue.ByUser> list) {
        // Tạo dataset cho biểu đồ cột
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        list.forEach(item -> dataset.addValue(item.getRevenue(), "Doanh thu", item.getUser()));

        // Tạo biểu đồ cột
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Nhân viên xuất sắc nhất", // Tiêu đề
                "Nhân viên",                     // Nhãn trục X
                "Doanh thu (VNĐ)",               // Nhãn trục Y
                dataset,                         // Dữ liệu
                PlotOrientation.VERTICAL,        // Hướng biểu đồ
                true, true, false                // Hiển thị legend, tooltips, URLs
        );

        // Tạo panel chứa biểu đồ
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(343, 190)); // Kích thước phù hợp với jPanel2

        // Xóa và thêm biểu đồ vào jPanel2
        BarChat.removeAll();
        BarChat.setLayout(new BorderLayout());
        BarChat.add(chartPanel, BorderLayout.CENTER);
        BarChat.revalidate();
        BarChat.repaint();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBegin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEnd = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        cboTimeRanges = new javax.swing.JComboBox<>();
        tabs = new javax.swing.JTabbedPane();
        RevenueByType = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblByCategory = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        donutChart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        RevenueByEmployee = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblByUser = new javax.swing.JTable();
        BarChat = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTotalAmountUser = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Từ ngày");

        jLabel2.setText("Đến ngày:");

        btnFilter.setBackground(new java.awt.Color(64, 189, 203));
        btnFilter.setText("Lọc");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        cboTimeRanges.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay" }));
        cboTimeRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTimeRangesActionPerformed(evt);
            }
        });

        tabs.setBackground(new java.awt.Color(255, 255, 255));
        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabsStateChanged(evt);
            }
        });

        RevenueByType.setBackground(new java.awt.Color(255, 255, 255));

        tblByCategory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Loại", "Doanh thu", "Số lượng", "Giá thấp nhất", "Giá cao nhất", "Giá trung bình"
            }
        ));
        jScrollPane2.setViewportView(tblByCategory);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Tổng quan:");

        donutChart.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Tổng doanh thu: ");

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout RevenueByTypeLayout = new javax.swing.GroupLayout(RevenueByType);
        RevenueByType.setLayout(RevenueByTypeLayout);
        RevenueByTypeLayout.setHorizontalGroup(
            RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
            .addGroup(RevenueByTypeLayout.createSequentialGroup()
                .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(donutChart, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        RevenueByTypeLayout.setVerticalGroup(
            RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByTypeLayout.createSequentialGroup()
                .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(donutChart, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Doanh thu từng loại", RevenueByType);

        RevenueByEmployee.setBackground(new java.awt.Color(255, 255, 255));

        tblByUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nhân viên", "Doanh thu", "Số bill", "Bill đầu tiên", "Bill cuối cùng"
            }
        ));
        tblByUser.setGridColor(new java.awt.Color(255, 204, 204));
        tblByUser.setSelectionBackground(new java.awt.Color(255, 204, 204));
        jScrollPane1.setViewportView(tblByUser);

        BarChat.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblTotalAmountUser.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalAmountUser.setForeground(new java.awt.Color(255, 0, 0));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Tổng doanh thu: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(lblTotalAmountUser, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalAmountUser, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tổng quan:");

        javax.swing.GroupLayout RevenueByEmployeeLayout = new javax.swing.GroupLayout(RevenueByEmployee);
        RevenueByEmployee.setLayout(RevenueByEmployeeLayout);
        RevenueByEmployeeLayout.setHorizontalGroup(
            RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                .addComponent(BarChat, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        RevenueByEmployeeLayout.setVerticalGroup(
            RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(BarChat, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabs.addTab("Doanh thu từng nhân viên", RevenueByEmployee);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tabs)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1)
                        .addGap(7, 7, 7)
                        .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilter)
                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabs))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
        this.selectTimeRange();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void cboTimeRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTimeRangesActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_cboTimeRangesActionPerformed

    private void tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
        // TODO add your handling code here:
        this.fillRevenue();
    }//GEN-LAST:event_tabsStateChanged

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
            java.util.logging.Logger.getLogger(RevenueManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RevenueManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RevenueManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RevenueManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RevenueManager dialog = new RevenueManager(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel BarChat;
    private javax.swing.JPanel RevenueByEmployee;
    private javax.swing.JPanel RevenueByType;
    private javax.swing.JButton btnFilter;
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JPanel donutChart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalAmountUser;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblByCategory;
    private javax.swing.JTable tblByUser;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtEnd;
    // End of variables declaration//GEN-END:variables
}
