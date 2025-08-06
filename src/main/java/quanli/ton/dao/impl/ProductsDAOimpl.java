package quanli.ton.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.entity.Product;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author huynhtrunghieu
 */
public class ProductsDAOimpl implements ProductsDAO {

    String insertSql = "INSERT INTO Products (Id, Name, Photo, Quantity, UnitPrice, ImportPrice, Discount, TypeId, ThickID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Products SET Name=?, Photo=?, Discount=?, TypeId=?, ThickID=? WHERE Id=?";
    String deleteSql = "DELETE FROM Products WHERE Id=?";
    String findAllSql = "SELECT * FROM Products";
    String findByIdSql = "SELECT * FROM Products WHERE Id=?";
    String importProduct = "{CALL ImportProduct(?, ?, ?)}";
    String findProductByNameSql = "SELECT * FROM Products WHERE Name = ?";
    String findProductByTypeSql = "SELECT * FROM Products WHERE TypeId = ?";
    String findProductByThickSql = "SELECT * FROM Products WHERE ThickID = ?";

    // Thêm các SQL cho inventory management
    String sellProductSql = "{CALL SellProduct(?, ?)}";

    @Override
    public void update(Product product) {
        Object[] args = {
            product.getName(), product.getPhoto(), product.getDiscount(), product.getTypeId(), product.getThickId(),
            product.getId()
        };
        XJdbc.executeUpdate(updateSql, args);
    }

    @Override
    public Product create(Product product) {
        Object[] args = {
            product.getId(), product.getName(), product.getPhoto(), 0,
            product.getUnitPrice(), 0, product.getDiscount(),
            product.getTypeId(), product.getThickId()
        }; //Quantity vaf ImportPrice sẽ để bằng 0 để lúc sau dùng importPrice để nhập hàng => ghi vào ProductHistoryPrice
        XJdbc.executeUpdate(insertSql, args);
        this.importProduct(product.getId(), product.getQuantity(), product.getImportPrice());
        return product;
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Product> findAll() {
        return XQuery.getBeanList(Product.class, findAllSql);
    }

    @Override
    public Product findById(String id) {
        return XQuery.getSingleBean(Product.class, findByIdSql, id);
    }

    @Override
    public void importProduct(String id, double quantity, double importPrice) {
        XJdbc.executeUpdate(importProduct, id, quantity, importPrice);
    }

    @Override
    public List<Product> findProductByName(String textInput) {
        return XQuery.getBeanList(Product.class, findProductByNameSql, textInput);
    }

    @Override
    public List<Product> findProductByType(String id) {
        return XQuery.getBeanList(Product.class, findProductByTypeSql, id);
    }

    @Override
    public List<Product> findProductByThick(int id) {
        return XQuery.getBeanList(Product.class, findProductByThickSql, id);
    }

    // ==================== INVENTORY MANAGEMENT METHODS ====================
    /**
     * Bán sản phẩm (sử dụng stored procedure để validate)
     *
     * @param productId ID sản phẩm
     * @param quantity Số lượng bán
     * @return true nếu thành công
     */
    @Override
    public void sellProduct(String productId, double quantity) {
        XJdbc.executeUpdate(sellProductSql, productId, quantity);
    }

    @Override
    public void returnProduct(String productId, double quantity) {
        XJdbc.executeUpdate("{CALL ReturnProduct(?, ?)}", productId, quantity);
    }

}
