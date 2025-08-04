/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

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

    String insertSql = "INSERT INTO Products (Id, Name, Photo, Quantity, UnitPrice, Discount, TypeId, ThickID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Products SET Name=?, Photo=?, Quantity=?, UnitPrice=?, Discount=?, TypeId=?, ThickID=? WHERE Id=?";
    String deleteSql = "DELETE FROM Products WHERE Id=?";
    String findAllSql = "SELECT * FROM Products";
    String findByIdSql = "SELECT * FROM Products WHERE Id=?";
    String importProduct =  "{CALL ImportProduct(?, ?, ?)}";
    String findProductByNameSql = "SELECT * FROM Products WHERE Name = ?";
    String findProductByTypeSql = "SELECT * FROM Products WHERE TypeId = ?";
    String findProductByThickSql = "SELECT * FROM Products WHERE ThickID = ?";


    @Override
    public void update(Product product) {
        Object[] args = {
            product.getName(), product.getPhoto(), product.getQuantity(),
            product.getUnitPrice(), product.getDiscount(), product.getTypeId(), product.getThickId(),
            product.getId()
        };
        XJdbc.executeUpdate(updateSql, args);
    }

    @Override
    public Product create(Product product) {
    Object[] args = {
            product.getId(), product.getName(), product.getPhoto(), product.getQuantity(),
            product.getUnitPrice(), product.getDiscount(), product.getTypeId(),product.getThickId()
        };
        XJdbc.executeUpdate(insertSql, args);
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
//    
//    public void importProduct(String id, long quantity) {
//        XJdbc.executeUpdate(insertSql, id, quantity);
//    }

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
    

    

}

