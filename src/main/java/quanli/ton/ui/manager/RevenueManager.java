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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.util.Collections;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import quanli.ton.util.XFile;

/**
 *
 * @author Admin
 */
public class RevenueManager extends javax.swing.JDialog implements RevenueController {

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
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        items.forEach(item -> dataset.setValue(item.getCategory(), item.getRevenue()));

        JFreeChart chart = ChartFactory.createPieChart(
                "Doanh thu từng loại", dataset, true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false); // bỏ viền ngoài

        // Màu sắc tùy chỉnh
        Paint[] colors = {
            new Color(0x4E79A7),
            new Color(0xF28E2B),
            new Color(0xE15759),
            new Color(0x76B7B2),
            new Color(0x59A14F),
            new Color(0xEDC948),
            new Color(0xB07AA1)
        };
        int i = 0;
        for (Object keyObj : dataset.getKeys()) {
            Comparable<?> key = (Comparable<?>) keyObj;
            plot.setSectionPaint(key, colors[i % colors.length]);
            i++;
        }

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        plot.setLabelPaint(Color.DARK_GRAY);
        plot.setLabelBackgroundPaint(new Color(50, 50, 50, 20)); // nền nhạt cho 
        plot.setLabelShadowPaint(null);

        // Đặt legend sang bên trái
        chart.setTitle((TextTitle) null);
        chart.getLegend().setPosition(RectangleEdge.LEFT);
        chart.setBackgroundPaint(Color.WHITE);               // nền toàn biểu đồ
        chart.getPlot().setBackgroundPaint(Color.WHITE);

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

        double totalCost = items.stream().mapToDouble(Revenue.ByCategory::getCost).sum();
        System.out.println(totalCost);
        System.out.println(total);
        double profit = total - totalCost;
        lblProfit.setText(moneyFormat.format(profit));
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

        // Chỉ chọn top 5 để vẽ biểu đồ
        Collections.sort(items, (o1, o2) -> Double.compare(o2.getRevenue(), o1.getRevenue()));
        List<Revenue.ByUser> top5Items = items.size() > 5 ? items.subList(0, 5) : items;
        addBarChart(top5Items);

        // 👉 Tổng doanh thu của tất cả nhân viên
        double total = items.stream().mapToDouble(Revenue.ByUser::getRevenue).sum();
        lblTotalAmountUser.setText(moneyFormat.format(total));
    }

    void productTypeFileExport() {
        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");
        String fileName = "DoanhThuTungLoai-" + txtBegin.getText() + "-" + txtEnd.getText();
        String[] header = new String[]{"Loại", "Doanh thu", "Số lượng", "Giá thấp nhất", "Giá cao nhất", "Giá trung bình", "Lợi nhuận"};
        List<Revenue.ByCategory> listObjs = dao.getByCategory(begin, end);
        String title = "Doanh thu của từng loại hàng - " + txtBegin.getText() + " - " + txtEnd.getText();
        XFile.fileExport(this, header, listObjs, fileName, title);
    }

    void EmployeeFileExport() {
        Date begin = XDate.parse(txtBegin.getText(), "MM/dd/yyyy");
        Date end = XDate.parse(txtEnd.getText(), "MM/dd/yyyy");
        String fileName = "DoanhThuTungNhanVien" + txtBegin.getText() + "-" + txtEnd.getText();
        String[] header = new String[]{"Nhân viên", "Doanh thu", "Số lượng bill", "Bill đầu tiên", "Bill cuối cùng"};
        List<Revenue.ByUser> listObjs = dao.getByUser(begin, end);
        String title = "Doanh thu của từng nhân viên - " + txtBegin.getText() + " - " + txtEnd.getText();
        XFile.fileExport(this, header, listObjs, fileName, title);
    }

    private void addBarChart(List<Revenue.ByUser> list) {
        // Tạo dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        list.forEach(item -> dataset.addValue(item.getRevenue(), "Doanh thu", item.getUser()));

        // Tạo biểu đồ
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Nhân viên xuất sắc nhất", "Nhân viên", "Doanh thu (VNĐ)", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        // Cải thiện hiển thị
        chart.setTitle((TextTitle) null);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Màu cột
        renderer.setSeriesPaint(0, new Color(0, 102, 102)); // xanh dương đẹp

        // Bo góc cột
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.1); // chiều rộng cột

        // Font trục
        Font axisFont = new Font("Segoe UI", Font.PLAIN, 12);
        plot.getDomainAxis().setLabelFont(axisFont);
        plot.getDomainAxis().setTickLabelFont(axisFont);
        plot.getRangeAxis().setLabelFont(axisFont);
        plot.getRangeAxis().setTickLabelFont(axisFont);

//    // Xoay nhãn trục X nếu cần
//    plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        // Nền trắng
        plot.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        // Chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(343, 190));

        // Gắn vào Panel
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        panel1 = new java.awt.Panel();
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
        tblByCategory = new javax.swing.JTable() {
            @Override
            public JTableHeader getTableHeader() {
                JTableHeader header = super.getTableHeader();
                header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
                header.setBackground(new java.awt.Color(224, 255, 255));  // pastel xanh ngọc
                header.setForeground(new java.awt.Color(0, 102, 102));    // xanh đậm
                ((javax.swing.table.DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(javax.swing.JLabel.CENTER);
                return header;
            }
        };
        jLabel3 = new javax.swing.JLabel();
        donutChart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblProfit = new javax.swing.JLabel();
        btnTypeExport = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        RevenueByEmployee = new javax.swing.JPanel();
        btnEmployeeExport = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblByUser = new javax.swing.JTable() {
            @Override
            public JTableHeader getTableHeader() {
                JTableHeader header = super.getTableHeader();
                header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
                header.setBackground(new java.awt.Color(224, 255, 255));  // pastel xanh ngọc
                header.setForeground(new java.awt.Color(0, 102, 102));    // xanh đậm
                ((javax.swing.table.DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(javax.swing.JLabel.CENTER);
                return header;
            }
        };
        BarChat = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTotalAmountUser = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Từ ngày:");

        txtBegin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBegin.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Đến ngày:");

        txtEnd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtEnd.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        btnFilter.setBackground(new java.awt.Color(0, 102, 102));
        btnFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFilter.setForeground(new java.awt.Color(255, 255, 255));
        btnFilter.setText("Lọc");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        cboTimeRanges.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Tổng lợi nhuận: ");

        lblProfit.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblProfit.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblProfit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        btnTypeExport.setBackground(new java.awt.Color(0, 102, 51));
        btnTypeExport.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTypeExport.setForeground(new java.awt.Color(255, 255, 255));
        btnTypeExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/excel.png"))); // NOI18N
        btnTypeExport.setText("Xuất file Excel");
        btnTypeExport.setBorderPainted(false);
        btnTypeExport.setDefaultCapable(false);
        btnTypeExport.setFocusable(false);
        btnTypeExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTypeExportActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("Doanh thu từng loại");

        javax.swing.GroupLayout RevenueByTypeLayout = new javax.swing.GroupLayout(RevenueByType);
        RevenueByType.setLayout(RevenueByTypeLayout);
        RevenueByTypeLayout.setHorizontalGroup(
            RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
            .addGroup(RevenueByTypeLayout.createSequentialGroup()
                .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(RevenueByTypeLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(donutChart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByTypeLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(51, 51, 51))))
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTypeExport, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        RevenueByTypeLayout.setVerticalGroup(
            RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByTypeLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(RevenueByTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))
                    .addGroup(RevenueByTypeLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(donutChart, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTypeExport, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        tabs.addTab("Doanh thu từng loại", RevenueByType);

        RevenueByEmployee.setBackground(new java.awt.Color(255, 255, 255));

        btnEmployeeExport.setBackground(new java.awt.Color(0, 102, 51));
        btnEmployeeExport.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEmployeeExport.setForeground(new java.awt.Color(255, 255, 255));
        btnEmployeeExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/excel.png"))); // NOI18N
        btnEmployeeExport.setText("Xuất file Excel");
        btnEmployeeExport.setBorderPainted(false);
        btnEmployeeExport.setDefaultCapable(false);
        btnEmployeeExport.setFocusable(false);
        btnEmployeeExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeExportActionPerformed(evt);
            }
        });

        tblByUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nhân viên", "Doanh thu", "Số lượng bill", "Bill đầu tiên", "Bill cuối cùng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblByUser.setGridColor(new java.awt.Color(255, 204, 204));
        tblByUser.setSelectionBackground(new java.awt.Color(255, 204, 204));
        jScrollPane1.setViewportView(tblByUser);

        BarChat.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setPreferredSize(new java.awt.Dimension(191, 92));

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
                        .addGap(26, 26, 26)
                        .addComponent(jLabel7))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblTotalAmountUser, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotalAmountUser, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tổng quan:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 102, 102));
        jLabel9.setText("Top 5 nhân viên xuất sắc nhất");

        javax.swing.GroupLayout RevenueByEmployeeLayout = new javax.swing.GroupLayout(RevenueByEmployee);
        RevenueByEmployee.setLayout(RevenueByEmployeeLayout);
        RevenueByEmployeeLayout.setHorizontalGroup(
            RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnEmployeeExport, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BarChat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(37, 37, 37)))))
                .addContainerGap())
        );
        RevenueByEmployeeLayout.setVerticalGroup(
            RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(RevenueByEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RevenueByEmployeeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RevenueByEmployeeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BarChat, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEmployeeExport, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
        );

        tabs.addTab("Doanh thu từng nhân viên", RevenueByEmployee);

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("THỐNG KÊ DOANH THU");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(267, 267, 267))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBegin, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTimeRanges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnTypePDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTypePDFActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnTypePDFActionPerformed

    private void btnEmployeePDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeePDFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEmployeePDFActionPerformed

    private void btnTypeExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTypeExportActionPerformed
        // TODO add your handling code here:ư
        productTypeFileExport();
    }//GEN-LAST:event_btnTypeExportActionPerformed

    private void btnEmployeeExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeExportActionPerformed
        // TODO add your handling code here:
        EmployeeFileExport();
    }//GEN-LAST:event_btnEmployeeExportActionPerformed

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
            java.util.logging.Logger.getLogger(RevenueManager.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
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
    private javax.swing.JButton btnEmployeeExport;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnTypeExport;
    private javax.swing.JComboBox<String> cboTimeRanges;
    private javax.swing.JPanel donutChart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblProfit;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalAmountUser;
    private java.awt.Panel panel1;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblByCategory;
    private javax.swing.JTable tblByUser;
    private javax.swing.JTextField txtBegin;
    private javax.swing.JTextField txtEnd;
    // End of variables declaration//GEN-END:variables
}
