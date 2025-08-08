/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package quanli.ton.ui.manager;

import quanli.ton.ui.components.ImportgoodsJDialog;
import quanli.ton.ui.components.PriceChartJDialog;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import quanli.ton.controller.ProductsController;
import quanli.ton.dao.ProductTypeDAO;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.dao.ThicknessDAO;
import quanli.ton.dao.impl.ProductTypeDAOImpl;
import quanli.ton.dao.impl.ProductsDAOimpl;
import quanli.ton.dao.impl.ThicknessDAOImpl;
import quanli.ton.entity.Product;
import quanli.ton.entity.ProductType;
import quanli.ton.entity.Thickness;
import quanli.ton.util.FileTypeFilter;
import quanli.ton.util.XDialog;
import quanli.ton.util.XIcon;
import quanli.ton.util.XStr;

/**
 *
 * @author huynhtrunghieu
 */
public class ProductManagerJDialog extends javax.swing.JDialog implements ProductsController {

    private ProductsDAO productsDAO = new ProductsDAOimpl();
    private ProductTypeDAO productTypeDAO = new ProductTypeDAOImpl();
    private ThicknessDAO thicknessDAO = new ThicknessDAOImpl();
    private List<Product> productList;
    private List<ProductType> typeList = List.of();
    private List<Thickness> thicknessList = List.of();
    File imageFile = null;
    double importQuantity = 0;

    /**
     * Creates new form Products
     */
    public ProductManagerJDialog(javax.swing.JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.open();
        initCustomUI();
    }

    private void initCustomUI() {
        jTabbedPane1.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    /**
     * Creates new form Products
     */
    @Override
    public void setForm(Product product) {
        txtId.setText(product.getId());
        txtName.setText(product.getName());
        txtQuantity.setText(String.format("%.2f", product.getQuantity()));
        slGiamGia.setValue((int) product.getDiscount());
        txtDiscountPercent.setText(product.getDiscount() + "%");
        txtUnitPrice.setText(String.valueOf(product.getUnitPrice()));
        txtCostPrice.setText(String.valueOf(product.getImportPrice()));

        XIcon.setIcon(lblImage, "images/products/" + product.getPhoto());
        imageFile = new File("images/products/" + product.getPhoto());

        if (product.getTypeId() != null) {
            ProductType selectedProductType = null;
            for (ProductType type : typeList) {
                if (type.getId().equals(product.getTypeId())) {
                    selectedProductType = type;
                    break;
                }
            }
            if (selectedProductType != null) {
                cboModel.setSelectedItem(selectedProductType.getName());
                fillcboThickness2(selectedProductType.getId()); // Gọi để điền cboThickness2 với các độ dày liên quan
            } else {
                cboModel.setSelectedIndex(0);
                fillcboThickness2(null); // Điền cboThickness2 với tất cả độ dày nếu không có loại nào được chọn
            }
        }

        // Lấy đối tượng Thickness trực tiếp từ DAO
        Thickness selectedThickness = thicknessDAO.findById(product.getThickId());

        if (selectedThickness != null) {
            // Đảm bảo cboThickness2 đã được điền trước khi setSelectedItem
            fillcboThickness2(product.getTypeId()); // Điền lại cboThickness2
            // Sau đó setSelectedItem
            cboThickness2.setSelectedItem(selectedThickness.getThick());
        }

    }

    @Override
    public Product getForm() {
        Product product = new Product();
        product.setId(txtId.getText());
        product.setName(txtName.getText());

        try {
            product.setQuantity(Double.parseDouble(txtQuantity.getText()));
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

        try {
            product.setImportPrice(Double.parseDouble(txtCostPrice.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số.");
            return null; // Trả về null nếu dữ liệu không hợp lệ
        }

        if (Double.parseDouble(txtCostPrice.getText()) > Double.parseDouble(txtUnitPrice.getText())) {
            if (!XDialog.confirm("Giá nhập sản phẩm hiện tại đang lớn hơn giá bán. Bạn có chắc chắn hay không")) {
                return null;
            }
        }
        product.setDiscount(slGiamGia.getValue());

        // Xử lý TypeId từ cboCategory
        int categoryIndex = cboModel.getSelectedIndex();
        product.setTypeId(typeList.get(categoryIndex).getId());

        // Xử lý ThickId từ cboThickness
        if (cboThickness2.getItemCount() > 0 && thicknessList != null) {
            String selectedThickName = (String) cboThickness2.getSelectedItem();
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

        if (imageFile == null) {
            imageFile = new File("images/products/" + product.getPhoto());
        }
        String imageName = this.saveFile(imageFile);
        product.setPhoto(imageName); // Giả sử ToolTipText lưu đường dẫn file ảnh
        return product;
    }

    @Override
    public void edit() {
        Product entity = productList.get(tblProduct.getSelectedRow());
        this.setForm(entity);
        this.setEditable(true);
        jTabbedPane1.setSelectedIndex(1);
    }

    public void open() {
        this.setLocationRelativeTo(null);
        productsDAO = new ProductsDAOimpl();
        this.clear();
        fillProductType();
        fillModel();
        fillToTable(null, null);
    }

    private void fillToTable(String productTypeId, Integer thicknessId) {
        DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
        model.setRowCount(0);
        List<Product> allProducts = productsDAO.findAll(); // Lấy tất cả sản phẩm ban đầu

        // Áp dụng bộ lọc
        List<Product> filteredList = new ArrayList<>();
        for (Product p : allProducts) { // Dùng allProducts ở đây
            boolean matchesType = (productTypeId == null || productTypeId.isEmpty() || p.getTypeId().equals(productTypeId));
            boolean matchesThickness = (thicknessId == null || p.getThickId() == null || p.getThickId().equals(thicknessId));

            if (matchesType && matchesThickness) {
                filteredList.add(p);
            }
        }

        // Cập nhật productList của class với danh sách đã lọc
        this.productList = filteredList; // DÒNG QUAN TRỌNG

        for (Product p : filteredList) {
            String typeName = productTypeDAO.findById(p.getTypeId()).getName();
            String thickName = "";
            if (p.getThickId() != null) {
                Thickness thick = thicknessDAO.findById(p.getThickId());
                thickName = (thick != null) ? thick.getThick() : ""; // Kiểm tra null để tránh NullPointerException
            }
            Object[] row = {
                p.getId(), p.getName(), p.getPhoto(), String.format("%.2f", p.getQuantity()), p.getUnitPrice(), p.getDiscount(),
                typeName, thickName, false // Giả định cột cuối cùng là boolean cho checkbox
            };
            model.addRow(row);
        }
//        updateNavigationButtons(); // Cập nhật trạng thái nút sau khi bảng được điền
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

    private void fillThickness(String typeId) {
        thicknessList = thicknessDAO.findByProductTypeId(typeId);
        cboThickness.removeAllItems(); // Sử dụng removeAllItems() cho JComboBox

        if (thicknessList.size() > 0) {
            cboThickness.addItem("Tất cả"); // Thêm tùy chọn "Tất cả" cho độ dày
            cboThickness.setSelectedIndex(0);
            for (Thickness p : thicknessList) {
                cboThickness.addItem(p.getThick());
            }
        } else {
            cboThickness.addItem("Không có độ dày"); // Xử lý trường hợp không có độ dày
        }
    }

    private void fillModel() {
        typeList = productTypeDAO.findAll();
        cboModel.removeAllItems();
        for (ProductType p : typeList) {
            cboModel.addItem(p.getName());
        }
        this.fillcboThickness2(typeList.get(0).getId());
    }

    private void fillcboThickness2(String typeId) {
        List<Thickness> thicknessesForForm;
        if (typeId != null && !typeId.isEmpty()) {
            thicknessesForForm = thicknessDAO.findByProductTypeId(typeId);
        } else {
            thicknessesForForm = thicknessDAO.findAll();
        }

        this.thicknessList = thicknessesForForm; // Đảm bảo thicknessList của lớp được cập nhật

        cboThickness2.removeAllItems();
//        cboThickness2.addItem(""); // Thêm một mục trống
        if (thicknessesForForm != null && !thicknessesForForm.isEmpty()) {
            for (Thickness thick : thicknessesForForm) {
                cboThickness2.addItem(thick.getThick());
            }
        }
    }

    @Override
    public void create() {
        if (!isValidInput()) {
            return;
        }

        Product newProduct = getForm();
        if (newProduct != null) {
            try {
                productsDAO.create(newProduct);
                XDialog.notify("Tạo sản phẩm thành công!");
                this.applyFilters();
                this.clear();
            } catch (Exception e) {
                XDialog.error("Lỗi khi tạo sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update() {
        if (!isValidInput()) {
            return;
        }

        Product updatedProduct = getForm();
        if (updatedProduct != null) {
            try {
                productsDAO.update(updatedProduct);
                XDialog.notify("Cập nhật sản phẩm thành công!");
                this.applyFilters();
                this.clear();
            } catch (Exception e) {
                XDialog.error("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete() {
        String productIdToDelete = txtId.getText();
        if (productIdToDelete.isEmpty()) {
            XDialog.alert("Vui lòng nhập Mã Sản Phẩm để xóa.");
            return;
        }

        if (XDialog.confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            try {
                productsDAO.deleteById(productIdToDelete);
                XDialog.notify("Xóa sản phẩm thành công!");
            } catch (Exception e) {
                XDialog.error("Lỗi khi xóa sản phẩm: " + e.getMessage());
                e.printStackTrace();
            }
        }

        this.applyFilters();
        this.clear();
    }

    @Override
    public void clear() {
        this.setForm(new Product());
        this.setEditable(false);
        imageFile = null;
    }

    @Override
    public void moveFirst() {
        this.moveTo(0);
    }

    @Override
    public void movePrevious() {
        this.moveTo(tblProduct.getSelectedRow() - 1);
    }

    @Override
    public void moveNext() {
        this.moveTo(tblProduct.getSelectedRow() + 1);
    }

    @Override
    public void moveLast() {
        this.moveTo(tblProduct.getRowCount() - 1);
    }

    @Override
    public void moveTo(int index) {
        if (index < 0) {
            this.moveLast();
        } else if (index >= tblProduct.getRowCount()) {
            this.moveFirst();
        } else {
            tblProduct.clearSelection();
            tblProduct.setRowSelectionInterval(index, index);
            this.edit();

            // Đảm bảo selection không bị mất
            if (tblProduct.getSelectedRow() != index) {
                tblProduct.setRowSelectionInterval(index, index);
            }
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

    @Override
    public File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh"); //Set title của cửa sổ jFileChooser
        fileChooser.setFileFilter(new FileTypeFilter(".png", "PNG File")); //Đặt các kiểu file có thể chọn
        fileChooser.setFileFilter(new FileTypeFilter(".jpg", "JPG File"));
        fileChooser.setFileFilter(new FileTypeFilter(".jpeg", "JPEG File"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            lblImage.setToolTipText(selectedFile.getName());
            XIcon.setIcon(lblImage, selectedFile);
            return selectedFile;
        }
        return null;
    }

    @Override
    public String saveFile(File selectedFile) {
        File file = XIcon.copyTo(selectedFile, "images/products/");
        return file.getName();
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

    @Override
    public void setEditable(boolean editable) {
        txtId.setEnabled(!editable);
        txtQuantity.setEnabled(!editable);
        txtCostPrice.setEnabled(!editable);
        btnCreate.setEnabled(!editable);
        btnUpdate.setEnabled(editable);
        btnDelete.setEnabled(editable);
        btnPriceChart.setEnabled(editable);
        btnImportGoods.setEnabled(editable);

        int rowCount = tblProduct.getRowCount();
        btnMoveFirst.setEnabled(editable && rowCount > 0);
        btnMovePrevious.setEnabled(editable && rowCount > 0);
        btnMoveNext.setEnabled(editable && rowCount > 0);
        btnMoveLast.setEnabled(editable && rowCount > 0);
    }

    @Override
    public boolean isValidInput() {
        return XStr.isBlank(txtId, "Mã sản phẩm không được để trống")
                && XStr.isBlank(txtName, "Tên sản phẩm không được để trống")
                && XStr.isBlank(txtQuantity, "Số lượng không được để trống")
                && XStr.isBlank(txtUnitPrice, "Đơn giá không được để trống");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable() {
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
        txtQuantity = new javax.swing.JTextField();
        txtDiscountPercent = new javax.swing.JLabel();
        slGiamGia = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMovePrevious = new javax.swing.JButton();
        btnMoveNext = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPriceChart = new javax.swing.JButton();
        btnImportGoods = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        cboThickness2 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtCostPrice = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton7.setBackground(new java.awt.Color(0, 102, 102));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Bỏ chọn tất cả");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 102, 102));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Xóa các mục đã chọn");
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
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblProduct);

        jButton6.setBackground(new java.awt.Color(0, 102, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Chọn tất cả");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        cboCategory.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCategoryActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Loại Sản Phẩm");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Loại Độ Dày");

        cboThickness.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboThickness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThicknessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboThickness, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(435, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap())
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DANH SÁCH", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Mã Sản Phẩm");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Tên Sản Phẩm");

        cboModel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboModelActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Đơn giá");

        txtId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtId.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        txtName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtName.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtQuantity.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        txtDiscountPercent.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtDiscountPercent.setText("Giảm giá");

        slGiamGia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        slGiamGia.setForeground(new java.awt.Color(0, 102, 102));
        slGiamGia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slGiamGiaStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Loại sản phẩm:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Loại độ dày:");

        btnClear.setBackground(new java.awt.Color(0, 102, 102));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Nhập Mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnMoveFirst.setBackground(new java.awt.Color(0, 102, 102));
        btnMoveFirst.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMoveFirst.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveFirst.setText("|<");
        btnMoveFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveFirstActionPerformed(evt);
            }
        });

        btnMovePrevious.setBackground(new java.awt.Color(0, 102, 102));
        btnMovePrevious.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMovePrevious.setForeground(new java.awt.Color(255, 255, 255));
        btnMovePrevious.setText("<<");
        btnMovePrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovePreviousActionPerformed(evt);
            }
        });

        btnMoveNext.setBackground(new java.awt.Color(0, 102, 102));
        btnMoveNext.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMoveNext.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveNext.setText(">>");
        btnMoveNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveNextActionPerformed(evt);
            }
        });

        btnMoveLast.setBackground(new java.awt.Color(0, 102, 102));
        btnMoveLast.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnMoveLast.setForeground(new java.awt.Color(255, 255, 255));
        btnMoveLast.setText(">|");
        btnMoveLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveLastActionPerformed(evt);
            }
        });

        btnCreate.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Tạo Mới");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Cập Nhật");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Xoá");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnPriceChart.setBackground(new java.awt.Color(204, 51, 51));
        btnPriceChart.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPriceChart.setForeground(new java.awt.Color(255, 255, 255));
        btnPriceChart.setText("Biến Động Giá");
        btnPriceChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPriceChartActionPerformed(evt);
            }
        });

        btnImportGoods.setBackground(new java.awt.Color(0, 122, 204));
        btnImportGoods.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnImportGoods.setForeground(new java.awt.Color(255, 255, 255));
        btnImportGoods.setText("Nhập Hàng Hoá");
        btnImportGoods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportGoodsActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Số Lượng");

        txtUnitPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUnitPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        cboThickness2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboThickness2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThickness2ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Giá nhập");

        txtCostPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCostPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 1, true), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboModel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtDiscountPercent)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(cboThickness2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnPriceChart, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnImportGoods)
                .addGap(62, 62, 62)
                .addComponent(slGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnCreate)
                .addGap(6, 6, 6)
                .addComponent(btnUpdate)
                .addGap(6, 6, 6)
                .addComponent(btnDelete)
                .addGap(6, 6, 6)
                .addComponent(btnClear)
                .addGap(33, 33, 33)
                .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(6, 6, 6)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(cboModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel7)
                                .addGap(6, 6, 6)
                                .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDiscountPercent))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(6, 6, 6)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(cboThickness2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel12)
                                .addGap(6, 6, 6)
                                .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPriceChart)
                            .addComponent(btnImportGoods))))
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMovePrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveNext, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMoveLast, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("BIỂU MẪU", jPanel2);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 102));
        jLabel11.setText("QUẢN LÝ SẢN PHẨM");

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(296, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(263, 263, 263))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane1)
                        .addComponent(jSeparator2))
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addContainerGap(466, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if (evt.getClickCount() == 2) {
            this.edit();
        }
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
        imageFile = this.chooseFile();
    }//GEN-LAST:event_lblImageMouseClicked

    private void slGiamGiaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slGiamGiaStateChanged
        // TODO add your handling code here:
        txtDiscountPercent.setText(slGiamGia.getValue() + "%");
    }//GEN-LAST:event_slGiamGiaStateChanged

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnMoveFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveFirstActionPerformed
        // TODO add your handling code here:
        moveFirst();
    }//GEN-LAST:event_btnMoveFirstActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        create();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPriceChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPriceChartActionPerformed
        // TODO add your handling code here:

        String productId = txtId.getText();

        if (productId == null || productId.trim().isEmpty()) {
            XDialog.alert("Vui lòng chọn một sản phẩm trước khi xem biến động giá!");
            return;
        }

        // Mở dialog PriceChartJDialog
        PriceChartJDialog priceChartDialog = new PriceChartJDialog(null, true);
        priceChartDialog.setProductId(productId);
        priceChartDialog.setVisible(true);
    }//GEN-LAST:event_btnPriceChartActionPerformed

    private void btnImportGoodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportGoodsActionPerformed
        // TODO add your handling code here:
        ImportgoodsJDialog importGoodsDialog = new ImportgoodsJDialog(new javax.swing.JFrame(), true);
        importGoodsDialog.setId(txtId.getText());
        importGoodsDialog.setProductName(txtName.getText());
        importGoodsDialog.setVisible(true);
        importGoodsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (importGoodsDialog.getImportQuantity() > 0 && importGoodsDialog.getImportPrice() > 0) {
                    importQuantity = importGoodsDialog.getImportQuantity();
                    txtQuantity.setText(String.format("%.2f", Double.parseDouble(txtQuantity.getText()) + importQuantity));
                    txtCostPrice.setText(String.valueOf(importGoodsDialog.getImportPrice()));
                }
            }
        });
    }//GEN-LAST:event_btnImportGoodsActionPerformed

    private void cboThicknessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThicknessActionPerformed
        // TODO add your handling code here:
        applyFilters();
    }//GEN-LAST:event_cboThicknessActionPerformed

    private void btnMovePreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovePreviousActionPerformed
        // TODO add your handling code here:
        movePrevious();
    }//GEN-LAST:event_btnMovePreviousActionPerformed

    private void btnMoveLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveLastActionPerformed
        // TODO add your handling code here:
        moveLast();
    }//GEN-LAST:event_btnMoveLastActionPerformed

    private void btnMoveNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveNextActionPerformed
        // TODO add your handling code here:
        moveNext();
    }//GEN-LAST:event_btnMoveNextActionPerformed

    private void cboModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboModelActionPerformed
        // TODO add your handling code here:
        ProductType type = typeList.get(cboModel.getSelectedIndex());
        this.fillcboThickness2(type.getId());
    }//GEN-LAST:event_cboModelActionPerformed

    private void cboThickness2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThickness2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboThickness2ActionPerformed

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
            java.util.logging.Logger.getLogger(ImportgoodsJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProductManagerJDialog dialog = new ProductManagerJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnImportGoods;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveNext;
    private javax.swing.JButton btnMovePrevious;
    private javax.swing.JButton btnPriceChart;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboModel;
    private javax.swing.JComboBox<String> cboThickness;
    private javax.swing.JComboBox<String> cboThickness2;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JSlider slGiamGia;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JLabel txtDiscountPercent;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fillToTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
