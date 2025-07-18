/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.dao.ProductsDAO;
import quanli.ton.entity.Product;
import quanli.ton.ui.manager.Products;
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

    @Override
    public void insert(Product product) {
        Object[] args = {
            product.getId(), product.getName(), product.getPhoto(), product.getQuantity(),
            product.getUnitPrice(), product.getDiscount(), product.getTypeId(),product.getThickId()
        };
        XJdbc.executeUpdate(insertSql, args);
    }

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
    public void delete(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Products> findAll() {
        return XQuery.getBeanList(Products.class, findAllSql);
    }

    @Override
    public Products findById(String id) {
        return XQuery.getSingleBean(Products.class, findByIdSql, id);
    }

    @Override
    public List<Product> selectAll() {
    return XQuery.getBeanList(Product.class, findAllSql);
    }

}

