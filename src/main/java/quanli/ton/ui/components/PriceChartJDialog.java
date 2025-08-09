/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package quanli.ton.ui.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import quanli.ton.dao.ProductPriceHistoryDAO;
import quanli.ton.dao.impl.ProductPriceHistoryDAOImpl;
import quanli.ton.entity.ProductPriceHistory;
import quanli.ton.util.XDialog;

/**
 *
 * @author huynhtrunghieu
 */
public class PriceChartJDialog extends javax.swing.JDialog {

    private String productId;
    private ProductPriceHistoryDAO productPriceHistoryDAO = new ProductPriceHistoryDAOImpl();

    public void setProductId(String productId) {
        this.productId = productId;
        createPriceChart();
    }

    private void createPriceChart() {
        if (productId == null || productId.trim().isEmpty()) {
            XDialog.alert("Vui lòng chọn một sản phẩm trước khi xem biến động giá!");
            return;
        }

        List<ProductPriceHistory> products = productPriceHistoryDAO.findAllById(productId);
        System.out.println("Found " + products.size() + " price history records for product: " + productId);

        if (products.isEmpty()) {
            XDialog.alert("Không có dữ liệu biến động giá cho sản phẩm này!");
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        products.forEach(item -> {
            Date effectiveDate = item.getEffectiveDate();
            String formattedDate = formatter.format(effectiveDate);
            System.out.println("Adding data: " + formattedDate + " - Import: " + item.getImportPrice() + ", Unit: " + item.getUnitPrice());

            dataset.setValue(item.getImportPrice(), "Giá nhập", formattedDate);
            dataset.setValue(item.getUnitPrice(), "Giá bán", formattedDate);
        });

        JFreeChart chart = ChartFactory.createLineChart(
                "Biểu đồ Giá Trị Tham Số", // Tiêu đề biểu đồ
                "Thời gian", // Nhãn trục X
                "Giá Trị", // Nhãn trục Y
                dataset, // Dữ liệu
                PlotOrientation.VERTICAL, // Hướng biểu đồ
                true, // Hiển thị chú giải (legend)
                true, // Tạo tooltips
                false // Tạo URLs
        );

        // Cài đặt màu nền tổng thể của biểu đồ
        chart.setBackgroundPaint(new Color(255, 255, 255));

        // Cài đặt màu tiêu đề
        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 18));

        // Truy cập CategoryPlot để tùy chỉnh các thuộc tính bên trong
        CategoryPlot plot = chart.getCategoryPlot();

        // Màu nền của vùng vẽ (plot area)
        plot.setBackgroundPaint(Color.WHITE);
        // Đường kẻ ngang (Range Gridlines)
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));

        // Màu viền vùng vẽ
        plot.setOutlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineStroke(new BasicStroke(1.0f));

        // Tùy chỉnh renderer để thay đổi style đường và thêm điểm đánh dấu
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // Tùy chỉnh "Giá nhập" (series 0)
        renderer.setSeriesPaint(0, new Color(255, 99, 71)); // Màu đỏ cam (Tomato)
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Tùy chỉnh "Giá bán" (series 1)
        renderer.setSeriesPaint(1, new Color(65, 105, 225)); // Màu xanh hoàng gia (RoyalBlue)
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, new java.awt.geom.Rectangle2D.Double(-3, -3, 6, 6));

        // Tùy chỉnh trục X (Category Axis)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Arial", Font.BOLD, 12));
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        // Tùy chỉnh trục Y (Value Axis - Range Axis)
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.BOLD, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        // Tùy chỉnh chú giải (Legend)
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));
        chart.getLegend().setBackgroundPaint(new Color(250, 250, 250, 180));
        chart.getLegend().setFrame(new org.jfree.chart.block.BlockBorder(Color.LIGHT_GRAY));

        // Tạo ChartPanel chứa biểu đồ
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Xóa layout cũ và thêm chartPanel
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        // Cài đặt kích thước dialog
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Creates new form PriceChart
     */
    public PriceChartJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(PriceChartJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PriceChartJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PriceChartJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PriceChartJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PriceChartJDialog dialog = new PriceChartJDialog(new javax.swing.JFrame(), true);
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
    // End of variables declaration//GEN-END:variables
}
