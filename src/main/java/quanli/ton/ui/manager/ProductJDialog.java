/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package quanli.ton.ui.manager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import quanli.ton.controller.ProductsController;
import quanli.ton.dao.ProductPriceHistoryDAO;
import quanli.ton.dao.ProductTypeDAO;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.dao.ThicknessDAO;
import quanli.ton.dao.impl.ProductPriceHistoryDAOImpl;
import quanli.ton.dao.impl.ProductTypeDAOImpl;
import quanli.ton.dao.impl.ProductsDAOimpl;
import quanli.ton.dao.impl.ThicknessDAOImpl;
import quanli.ton.entity.Product;
import quanli.ton.entity.ProductPriceHistory;
import quanli.ton.entity.ProductType;
import quanli.ton.entity.Thickness;
import quanli.ton.util.XDialog;

/**
 *
 * @author huynhtrunghieu
 */
public class ProductJDialog extends javax.swing.JDialog implements ProductsController {
    
    private ProductsDAO productsDAO = new ProductsDAOimpl();
    private ProductTypeDAO productTypeDAO = new ProductTypeDAOImpl();
    private ThicknessDAO thicknessDAO = new ThicknessDAOImpl();
    private ProductPriceHistoryDAO ProductPriceHistoryDAO = new ProductPriceHistoryDAOImpl();
    private int currentIndex = 0; 
    private List<Product> productList; 
    private List<ProductType> typeList = List.of();
    private List<Thickness> thicknessList = List.of();

    /**
     * Creates new form Products
     */
    public ProductJDialog(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal); 
        initComponents(); 
        this.open();
        initCustomUI(); 
    }  

     private void initCustomUI() {
        // Set background color for the dialog itself
        getContentPane().setBackground(new Color(240, 240, 240)); // Light Grayish

        // JTabbedPane styling
        jTabbedPane1.setBackground(new Color(255, 255, 255)); // White
        jTabbedPane1.setForeground(new Color(50, 50, 50)); // Dark Gray text
        jTabbedPane1.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Panel backgrounds
        jPanel1.setBackground(new Color(250, 250, 250)); // Slightly off-white
        jPanel2.setBackground(new Color(250, 250, 250));

        // Table Styling (jPanel1)
        tblProduct.getTableHeader().setBackground(new Color(70, 130, 180)); // SteelBlue
        tblProduct.getTableHeader().setForeground(Color.WHITE);
        tblProduct.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblProduct.setRowHeight(25);
        tblProduct.setGridColor(new Color(220, 220, 220)); // Light gray grid
        tblProduct.setSelectionBackground(new Color(173, 216, 230)); // LightBlue for selection
        tblProduct.setSelectionForeground(Color.BLACK);

        // Alternating row colors for JTable
        tblProduct.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                }
                return c;
            }
        });

        // Styling for JLabels, JTextFields, JComboBoxes (jPanel2)
        Color labelColor = new Color(50, 50, 50);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        Color borderColor = new Color(180, 180, 180); // Light gray border

        jLabel2.setForeground(labelColor); jLabel2.setFont(labelFont);
        jLabel3.setForeground(labelColor); jLabel3.setFont(labelFont);
        jLabel4.setForeground(labelColor); jLabel4.setFont(labelFont);
        txtDiscountPercent.setForeground(labelColor); txtDiscountPercent.setFont(labelFont);
        jLabel6.setForeground(labelColor); jLabel6.setFont(labelFont);
        jLabel1.setForeground(labelColor); jLabel1.setFont(labelFont);
        jLabel5.setForeground(labelColor); jLabel5.setFont(labelFont);
        jLabel8.setForeground(labelColor); jLabel8.setFont(labelFont);
        jLabel7.setForeground(labelColor); jLabel7.setFont(labelFont);

        txtId.setFont(textFieldFont); txtId.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        txtName.setFont(textFieldFont); txtName.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        txtUnitPrice.setFont(textFieldFont); txtUnitPrice.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        jTextField1.setFont(textFieldFont); jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        jTextField2.setFont(textFieldFont); jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        jTextField3.setFont(textFieldFont); jTextField3.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        jTextField4.setFont(textFieldFont); jTextField4.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));

        cboModel.setFont(textFieldFont); cboModel.setBackground(Color.WHITE); cboModel.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        cboCategory.setFont(labelFont); cboCategory.setBackground(Color.WHITE); cboCategory.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
        cboThickness.setFont(labelFont); cboThickness.setBackground(Color.WHITE); cboThickness.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));

        // Slider styling
        slGiamGia.setBackground(new Color(230, 230, 230)); // Light gray
        slGiamGia.setForeground(new Color(100, 100, 100)); // Darker gray for ticks

        // lblImage border and initial text
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("Click to choose image");
        lblImage.setForeground(new Color(150, 150, 150)); // Gray text for placeholder

        // Button Styling
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);

        // Action Buttons
        btnCreate1.setBackground(new Color(46, 139, 87)); // SeaGreen
        btnCreate1.setForeground(Color.WHITE);
        btnCreate1.setFont(buttonFont);
        btnCreate1.setFocusPainted(false);
        btnCreate1.setBorderPainted(false);

        btnUpdate1.setBackground(new Color(255, 165, 0)); // Orange
        btnUpdate1.setForeground(Color.WHITE);
        btnUpdate1.setFont(buttonFont);
        btnUpdate1.setFocusPainted(false);
        btnUpdate1.setBorderPainted(false);

        btnDelete1.setBackground(new Color(220, 20, 60)); // Crimson
        btnDelete1.setForeground(Color.WHITE);
        btnDelete1.setFont(buttonFont);
        btnDelete1.setFocusPainted(false);
        btnDelete1.setBorderPainted(false);

        btnClear1.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        btnClear1.setForeground(Color.WHITE);
        btnClear1.setFont(buttonFont);
        btnClear1.setFocusPainted(false);
        btnClear1.setBorderPainted(false);

        // Navigation Buttons
        Color navButtonBg = new Color(70, 130, 180); // SteelBlue
        Color navButtonFg = Color.WHITE;

        // Specific buttons
        jButton1.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        jButton1.setForeground(Color.WHITE);
        jButton1.setFont(buttonFont);
        jButton1.setFocusPainted(false);
        jButton1.setBorderPainted(false);

        ImportGoods.setBackground(new Color(30, 144, 255)); // DodgerBlue
        ImportGoods.setForeground(Color.WHITE);
        ImportGoods.setFont(buttonFont);
        ImportGoods.setFocusPainted(false);
        ImportGoods.setBorderPainted(false);

        // Check/Uncheck buttons
        Color checkButtonBg = new Color(112, 128, 144); // SlateGray
        jButton6.setBackground(checkButtonBg); jButton6.setForeground(Color.WHITE); jButton6.setFont(buttonFont); jButton6.setFocusPainted(false); jButton6.setBorderPainted(false);
        jButton7.setBackground(checkButtonBg); jButton7.setForeground(Color.WHITE); jButton7.setFont(buttonFont); jButton7.setFocusPainted(false); jButton7.setBorderPainted(false);
        jButton8.setBackground(checkButtonBg); jButton8.setForeground(Color.WHITE); jButton8.setFont(buttonFont); jButton8.setFocusPainted(false); jButton8.setBorderPainted(false);
    }
     
    @Override
    public void fillBillDetails() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void selectTimeRange() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * Creates new form Products
     */


    private void fillComboBox() {
    cboModel.removeAllItems();
    cboModel.addItem(""); // Mục trống để người dùng có thể không chọn
    List<Product> types = productsDAO.findAll(); // Giả sử có phương thức này
    for (Product type : types) {
        cboModel.addItem(type.getId());
    }
}

    private void setForm(Product product) {
    txtId.setText(product.getId());
    txtName.setText(product.getName());
    txtUnitPrice.setText(String.valueOf(product.getUnitPrice()));
    slGiamGia.setValue((int) product.getDiscount());
    txtDiscountPercent.setText(product.getDiscount() + "%");

    cboModel.setSelectedItem(product.getTypeId());
    jTextField1.setText(product.getTypeId());
    jTextField2.setText(String.valueOf(product.getThickId())); // thickId là số nguyên
    jTextField3.setText(String.valueOf(product.getQuantity()));

    if (product.getPhoto() != null && !product.getPhoto().isEmpty()) {
        try {
            ImageIcon icon = new ImageIcon(product.getPhoto());
            lblImage.setIcon(icon);
            lblImage.setText(""); // Xóa bất kỳ văn bản nào
        } catch (Exception e) {
            lblImage.setIcon(null);
            lblImage.setText("Không có hình ảnh");
            e.printStackTrace();
        }
    } else {
        lblImage.setIcon(null);
        lblImage.setText("Không có hình ảnh");
    }
}

private Product getForm() {
        Product product = new Product();
        product.setId(txtId.getText());
        product.setName(txtName.getText());
        product.setPhoto(lblImage.getToolTipText()); // Giả sử ToolTipText lưu đường dẫn file ảnh

        try {
            product.setQuantity(Integer.parseInt(jTextField4.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên.");
            return null; // Trả về null nếu dữ liệu không hợp lệ
        }

        try {
            product.setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số.");
            return null; // Trả về null nếu dữ liệu không hợp lệ
        }

        product.setDiscount(slGiamGia.getValue());

        // Xử lý TypeId từ cboCategory
        int categoryIndex = cboCategory.getSelectedIndex();
        if (categoryIndex > 0) { // Nếu một loại sản phẩm cụ thể được chọn (không phải "Tất cả")
            // Lấy ID từ đối tượng ProductType trong typeList
            product.setTypeId(typeList.get(categoryIndex - 1).getId());
        } else {
            // Nếu "Tất cả" được chọn, đây không phải là một loại sản phẩm hợp lệ để tạo mới.
            // Hiển thị thông báo lỗi và trả về null để ngăn chặn việc tạo/cập nhật.
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Loại Sản Phẩm cụ thể.");
            return null;
        }

        // Xử lý ThickId từ cboThickness
        if (cboThickness.getSelectedIndex() > 0 && cboThickness.getItemCount() > 0 && thicknessList != null) {
            String selectedThickName = (String) cboThickness.getSelectedItem();
            Integer selectedThickId = null;
            for (Thickness thick : thicknessList) {
                if (thick.getThick().equals(selectedThickName)) {
                    selectedThickId = thick.getId();
                    break;
                }
            }
            product.setThickId(selectedThickId);
        } else {
            // Nếu "Tất cả" hoặc không có lựa chọn độ dày, gán null
            product.setThickId(null);
        }

        return product;
    }

    private void edit() {
        int selectedRow = tblProduct.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < productList.size()) {
            Product product = productList.get(selectedRow);
            setForm(product);
            jTabbedPane1.setSelectedIndex(1); // Chuyển sang tab form (tab2)
            currentIndex = selectedRow;
            updateNavigationButtons();
        }
    }

    
    public void open() {
    // TODO: Hiển thị dữ liệu từ DB ra bảng
        productsDAO = new ProductsDAOimpl(); 
        fillComboBox();
        fillProductType();
        updateNavigationButtons(); 
    }
    
    private void fillToTable(String productTypeId, Integer thicknessId) { // Thêm tham số lọc
    DefaultTableModel model = (DefaultTableModel)tblProduct.getModel();
    model.setRowCount(0);
    List<Product> list = productsDAO.findAll(); // Lấy tất cả sản phẩm ban đầu

    // Áp dụng bộ lọc
    List<Product> filteredList = new ArrayList<>();
    for (Product p : list) {
        boolean matchesType = (productTypeId == null || productTypeId.isEmpty() || p.getTypeId().equals(productTypeId));
        boolean matchesThickness = (thicknessId == null || p.getThickId() == null || p.getThickId().equals(thicknessId));

        if (matchesType && matchesThickness) {
            filteredList.add(p);
        }
    }

    for (Product p : filteredList) {
        String thickName = "";
        if (p.getThickId() != null) {
            Thickness thick = thicknessDAO.findById(p.getThickId());
            thickName = thick.getThick();
        }
        Object[] row = {
            p.getId(), p.getName(), p.getPhoto(), p.getQuantity(), p.getUnitPrice(), p.getDiscount(),
            p.getTypeId(), thickName
        };
        model.addRow(row);
    }
}
     private void fillProductType() {
    typeList = productTypeDAO.findAll();
    cboCategory.removeAllItems();
    cboCategory.addItem("Tất cả");
    cboCategory.setSelectedIndex(0);
    for (ProductType p : typeList) {
        cboCategory.addItem(p.getName());
    }
}
    private void fillThickness(String typeId){
        thicknessList = thicknessDAO.findByProductTypeId(typeId);
        cboThickness.removeAllItems(); // Sử dụng removeAllItems() cho JComboBox

    if (thicknessList.size() > 0){
       cboThickness.addItem("Tất cả"); // Thêm tùy chọn "Tất cả" cho độ dày
       cboThickness.setSelectedIndex(0);
        for (Thickness p : thicknessList) {
            cboThickness.addItem(p.getThick());
        }
    } else {
        cboThickness.addItem("Không có độ dày"); // Xử lý trường hợp không có độ dày
    }
    }
    
    private void create() {
        Product newProduct = getForm();
        if (newProduct != null) {
            try {
                productsDAO.create(newProduct); 
                JOptionPane.showMessageDialog(this, "Sản phẩm đã được tạo thành công!");
                clear(); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void update() {
        Product updatedProduct = getForm();
        if (updatedProduct != null) {
            try {
                productsDAO.update(updatedProduct); // Cập nhật sản phẩm
                JOptionPane.showMessageDialog(this, "Sản phẩm đã được cập nhật thành công!");
            }  catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void delete() {
        String productIdToDelete = txtId.getText();
        if (productIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Sản Phẩm để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productsDAO.deleteById(productIdToDelete); // Xóa sản phẩm
                JOptionPane.showMessageDialog(this, "Sản phẩm đã được xóa thành công!");
                clear(); // Xóa form
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clear() {
        txtId.setText("");
        txtName.setText("");
        txtUnitPrice.setText("");
        slGiamGia.setValue(0);
        txtDiscountPercent.setText("0%");
        cboModel.setSelectedIndex(0); // Đặt lại combo box
        jTextField1.setText(""); // Xóa trường ID danh mục
        jTextField2.setText(""); // Xóa trường ID độ dày
        jTextField3.setText(""); // Xóa trường số lượng
        lblImage.setIcon(null);
        lblImage.setText("Nhấp để chọn hình ảnh");
        currentIndex = -1; // Không có sản phẩm nào được chọn
        updateNavigationButtons();
    }

    private void moveFirst() {
        if (!productList.isEmpty()) {
            currentIndex = 0;
            setForm(productList.get(currentIndex));
            updateNavigationButtons();
        }
    }

    private void movePrevious() {
        if (!productList.isEmpty() && currentIndex > 0) {
            currentIndex--;
            setForm(productList.get(currentIndex));
            updateNavigationButtons();
        }
    }

    private void moveNext() {
        if (!productList.isEmpty() && currentIndex < productList.size() - 1) {
            currentIndex++;
            setForm(productList.get(currentIndex));
            updateNavigationButtons();
        }
    }

    private void moveLast() {
        if (!productList.isEmpty()) {
            currentIndex = productList.size() - 1;
            setForm(productList.get(currentIndex));
            updateNavigationButtons();
        }
    }

public void checkAll() {
    this.setCheckedAll(true);
}

public void uncheckAll() {
     this.setCheckedAll(false);
}
 private void setCheckedAll(boolean checked) {
        for (int i = 0; i < tblProduct.getRowCount(); i++) {
            tblProduct.setValueAt(checked, i, 8); 
        }
    }
public void deleteCheckedItems() {
    if (XDialog.confirm("Bạn thực sự muốn xóa các mục chọn?")) {
        DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 8)) { 
                String productId = (String) model.getValueAt(i, 0); 
                productsDAO.deleteById(productId); 
            }
        }
        this.fillProductType(); 
    }
}

    private void updateNavigationButtons() {
        boolean enableNav = productList != null && !productList.isEmpty();
        btnMoveFirst.setEnabled(enableNav && currentIndex > 0);
        btnMovePrevious.setEnabled(enableNav && currentIndex > 0);
        btnMoveNext.setEnabled(enableNav && currentIndex < productList.size() - 1);
        btnMoveLast.setEnabled(enableNav && currentIndex < productList.size() - 1);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Lưu đường dẫn vào đối tượng sản phẩm của bạn hoặc một biến tạm thời
            // Hiện tại, chỉ hiển thị nó.
            try {
                ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                lblImage.setIcon(icon);
                lblImage.setText(""); // Xóa bất kỳ văn bản nào
                // Bạn có thể muốn lưu selectedFile.getAbsolutePath() vào một biến
                // sẽ được lưu cùng với sản phẩm.
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải hình ảnh: " + e.getMessage());
                lblImage.setIcon(null);
                lblImage.setText("Không có hình ảnh");
                e.printStackTrace();
            }
        }
    }
    
     private void applyFilters() {
        String selectedProductTypeId = null;
        Integer selectedThicknessId = null;

        // Lấy TypeId từ cboCategory
        int categoryIndex = cboCategory.getSelectedIndex();
        if (categoryIndex > 0) { // Nếu không phải "Tất cả"
            ProductType selectedType = typeList.get(categoryIndex - 1);
            selectedProductTypeId = selectedType.getId();
        }

        // Lấy ThicknessId từ cboThickness
        int thicknessIndex = cboThickness.getSelectedIndex();
        if (thicknessIndex > 0 && cboThickness.getItemCount() > 0) { // Nếu không phải "Tất cả" và có mục nào đó
            String selectedThickName = (String) cboThickness.getSelectedItem();
            // Tìm Thickness object từ thicknessList dựa vào selectedThickName để lấy ID
            for (Thickness thick : thicknessList) {
                if (thick.getThick().equals(selectedThickName)) {
                    selectedThicknessId = thick.getId();
                    break;
                }
            }
        }
        fillToTable(selectedProductTypeId, selectedThicknessId);
    }
    
    private void showPriceChart (){
        PriceChart dialog = new PriceChart((Frame)this.getOwner(),true);
        dialog.setProductId(txtId.getText()); 
        dialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        cboCategory = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboThickness = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cboModel = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtUnitPrice = new javax.swing.JTextField();
        txtDiscountPercent = new javax.swing.JLabel();
        slGiamGia = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        btnClear1 = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        btnCreate1 = new javax.swing.JButton();
        btnUpdate1 = new javax.swing.JButton();
        btnDelete1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        ImportGoods = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton7.setText("Bỏ chọn tất cả");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Bỏ các mục chọn");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "PhoTo", "Số Lượng Sản Phẩm", "Đơn Giá", "Giảm Giá", "Loại hàng", "Độ Dày", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblProduct);

        jButton6.setText("Chọn tất cả");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        cboCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoryActionPerformed(evt);
            }
        });

        jLabel9.setText("Loại Sản Phẩm");

        jLabel10.setText("Loại Độ Dày");

        cboThickness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThicknessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addGap(46, 46, 46))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addGap(51, 51, 51))
        );

        jTabbedPane1.addTab("Danh Sách ", jPanel1);

        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        jLabel2.setText("Mã Sản Phẩm");

        jLabel3.setText("Tên Sản Phẩm");

        jLabel4.setText("Đơn giá");

        txtDiscountPercent.setText("Giảm giá");

        slGiamGia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slGiamGiaStateChanged(evt);
            }
        });

        jLabel6.setText("Loại");

        jLabel1.setText("Mã Loại Sản Phẩm");

        jLabel5.setText("Mã Độ Dày");

        jLabel8.setText("Nhập Thêm");

        btnClear1.setText("Nhập Mới");
        btnClear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClear1ActionPerformed(evt);
            }
        });

        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        btnMovePrevious.setText("<<");

        btnMoveNext.setText(">>");

        btnMoveLast.setText(">|");

        btnCreate1.setText("Tạo Mới");
        btnCreate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate1ActionPerformed(evt);
            }
        });

        btnUpdate1.setText("Cập Nhật");
        btnUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate1ActionPerformed(evt);
            }
        });

        btnDelete1.setText("Xoá");
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });

        jButton1.setText("Biến Động Giá");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ImportGoods.setText("Nhập Hàng Hoá");
        ImportGoods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportGoodsActionPerformed(evt);
            }
        });

        jLabel7.setText("Số Lượng");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtId, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                                .addComponent(txtUnitPrice, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboModel, javax.swing.GroupLayout.Alignment.LEADING, 0, 138, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel1)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtDiscountPercent)
                                        .addGap(84, 84, 84))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(slGiamGia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(64, 64, 64)
                                        .addComponent(jLabel7)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ImportGoods))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCreate1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear1)
                        .addGap(55, 55, 55)
                        .addComponent(btnMoveFirst)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMovePrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveLast)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoveNext)
                        .addGap(0, 21, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDiscountPercent))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(slGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(ImportGoods))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate1)
                    .addComponent(btnUpdate1)
                    .addComponent(btnDelete1)
                    .addComponent(btnClear1)
                    .addComponent(btnMoveFirst)
                    .addComponent(btnMovePrevious)
                    .addComponent(btnMoveLast)
                    .addComponent(btnMoveNext))
                .addGap(109, 109, 109))
        );

        jTabbedPane1.addTab("Biểu Mẫu", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        uncheckAll();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        deleteCheckedItems();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void tblProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseClicked
        // TODO add your handling code here:
        this.edit();
    }//GEN-LAST:event_tblProductMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        checkAll();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void cboCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCategoryActionPerformed
        // TODO add your handling code here:
        int categoryIndex = cboCategory.getSelectedIndex();
        String selectedProductTypeId = null;
        cboThickness.removeAllItems(); // Xóa các mục hiện có trong cboThickness

        if (categoryIndex > 0) { // Nếu không phải "Tất cả"
            ProductType selectedType = typeList.get(categoryIndex - 1);
            selectedProductTypeId = selectedType.getId();
            fillThickness(selectedProductTypeId); // Điền cboThickness dựa trên loại đã chọn
        } else { // "Tất cả" được chọn
            cboThickness.addItem("Tất cả");
            cboThickness.setSelectedIndex(0);
            // Nếu "Tất cả" loại sản phẩm được chọn, không cần lọc theo loại sản phẩm cụ thể
        }
        // Gọi lại fillToTable để cập nhật bảng với bộ lọc mới
        applyFilters();
    }//GEN-LAST:event_cboCategoryActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        // TODO add your handling code here:
        this.chooseFile();
    }//GEN-LAST:event_lblImageMouseClicked

    private void slGiamGiaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slGiamGiaStateChanged
        // TODO add your handling code here:
        txtDiscountPercent.setText(slGiamGia.getValue() + "%");
    }//GEN-LAST:event_slGiamGiaStateChanged

    private void btnClear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClear1ActionPerformed
        // TODO add your handling code here:
        clear ();
    }//GEN-LAST:event_btnClear1ActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnCreate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate1ActionPerformed
        // TODO add your handling code here:
        create ();
    }//GEN-LAST:event_btnCreate1ActionPerformed

    private void btnUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate1ActionPerformed
        // TODO add your handling code here:
        update ();
    }//GEN-LAST:event_btnUpdate1ActionPerformed

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        // TODO add your handling code here:
        delete ();
    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        List<ProductPriceHistory> products = ProductPriceHistoryDAO.findAllById(txtId.getText());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        products.forEach(item -> {
            Date effectiveDate = item.getEffectiveDate();
            String formattedDate = formatter.format(effectiveDate);

            dataset.setValue(item.getImportPrice(), "Giá nhập", formattedDate);
            dataset.setValue(item.getUnitPrice(), "Giá bán", formattedDate);
        });

        JFreeChart chart = ChartFactory.createLineChart(
            "Biểu đồ Giá Trị Tham Số", // Tiêu đề biểu đồ
            "Thời gian",               // Nhãn trục X (Tham Số -> Thời gian cho rõ ràng)
            "Giá Trị",                 // Nhãn trục Y
            dataset,                   // Dữ liệu
            PlotOrientation.VERTICAL,  // Hướng biểu đồ
            true,                      // Hiển thị chú giải (legend)
            true,                      // Tạo tooltips
            false                      // Tạo URLs
        );

        // 1. Cài đặt màu nền tổng thể của biểu đồ
        chart.setBackgroundPaint(new Color(245, 245, 245)); // Màu xám nhạt

        // 2. Cài đặt màu tiêu đề
        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 18)); // Đặt font cho tiêu đề

        // 3. Truy cập CategoryPlot để tùy chỉnh các thuộc tính bên trong
        CategoryPlot plot = chart.getCategoryPlot();

        // 4. Màu nền của vùng vẽ (plot area)
        plot.setBackgroundPaint(Color.WHITE); // Màu trắng cho nền vùng vẽ

        // 5. Đường kẻ ngang (Range Gridlines)
        plot.setRangeGridlinePaint(new Color(200, 200, 200)); // Màu xám nhạt hơn
        plot.setRangeGridlineStroke(new BasicStroke(0.5f)); // Độ dày đường kẻ

        // 6. Đường kẻ dọc (Domain Gridlines - nếu muốn hiển thị)
        // plot.setDomainGridlinePaint(new Color(200, 200, 200)); // Uncomment nếu muốn hiển thị đường kẻ dọc
        // plot.setDomainGridlineStroke(new BasicStroke(0.5f));

        // 7. Màu viền vùng vẽ
        plot.setOutlinePaint(Color.LIGHT_GRAY); // Màu viền xám nhạt
        plot.setOutlineStroke(new BasicStroke(1.0f)); // Độ dày viền

        // 8. Tùy chỉnh renderer để thay đổi style đường và thêm điểm đánh dấu
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // Tùy chỉnh "Giá nhập" (series 0)
        renderer.setSeriesPaint(0, new Color(255, 99, 71)); // Màu đỏ cam (Tomato)
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Độ dày đường
        renderer.setSeriesShapesVisible(0, true); // Hiển thị hình dạng tại các điểm dữ liệu
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6)); // Hình tròn nhỏ

        // Tùy chỉnh "Giá bán" (series 1)
        renderer.setSeriesPaint(1, new Color(65, 105, 225)); // Màu xanh hoàng gia (RoyalBlue)
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, new java.awt.geom.Rectangle2D.Double(-3, -3, 6, 6)); // Hình vuông nhỏ

        // 9. Tùy chỉnh trục X (Category Axis)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Arial", Font.BOLD, 12)); // Font cho nhãn trục X
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10)); // Font cho các giá trị trên trục X
        //domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45); // Xoay nhãn 45 độ nếu dài để tránh chồng lấn

        // 10. Tùy chỉnh trục Y (Value Axis - Range Axis)
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.BOLD, 12)); // Font cho nhãn trục Y
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 10)); // Font cho các giá trị trên trục Y

        // 11. Tùy chỉnh chú giải (Legend)
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12)); // Font cho chú giải
        chart.getLegend().setBackgroundPaint(new Color(250, 250, 250, 180)); // Nền chú giải hơi trong suốt
        chart.getLegend().setFrame(new org.jfree.chart.block.BlockBorder(Color.LIGHT_GRAY)); // Viền cho chú giải

        // 12. Hiển thị biểu đồ
        ChartFrame frame = new ChartFrame("Biểu Đồ Thông Số Sản Phẩm", chart);
        frame.setVisible(true);
        frame.setSize(800, 600); // Tăng kích thước khung hình
        frame.setLocationRelativeTo(null);
        this.showPriceChart(); 
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ImportGoodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportGoodsActionPerformed
        // TODO add your handling code here:
        Importgoods importGoodsFrame = new Importgoods();
        importGoodsFrame.setVisible(true);
    }//GEN-LAST:event_ImportGoodsActionPerformed

    private void cboThicknessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThicknessActionPerformed
        // TODO add your handling code here:
        applyFilters();
    }//GEN-LAST:event_cboThicknessActionPerformed

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
            java.util.logging.Logger.getLogger(ProductJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProductJDialog dialog = new ProductJDialog(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton ImportGoods;
    private javax.swing.JButton btnClear1;
    private javax.swing.JButton btnCreate1;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnUpdate1;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboModel;
    private javax.swing.JComboBox<String> cboThickness;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblImage;
    private javax.swing.JSlider slGiamGia;
    private javax.swing.JTable tblProduct;
    private javax.swing.JLabel txtDiscountPercent;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
