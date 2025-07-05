/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.dao.ProductTypeDAO;
import quanli.ton.entity.ProductType;

import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author USER
 */
public class ProductTypeDAOImpl implements ProductTypeDAO{
    String createSql = "INSERT INTO ProductType(Id, Name, RequiresSize, DefaultLength) VALUES(?, ?, ?, ?)";
    String updateSql = "UPDATE ProductType SET Name=?, RequiresSize=?, DefaultLength=? WHERE Id=?";
    String deleteSql = "DELETE FROM ProductType WHERE Id=?";
    String findAllSql = "SELECT * FROM ProductType";
    String findByIdSql = "SELECT * FROM ProductType WHERE Id=?";
    
    @Override
    public ProductType create(ProductType entity) {
        Object[] values = {
            entity.getId(),
            entity.getName(),
            entity.isRequiresSize(),
            entity.getDefaultLength()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(ProductType entity) {
        Object[] values = {
            entity.getId(),
            entity.getName(),
            entity.isRequiresSize(),
            entity.getDefaultLength()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<ProductType> findAll() {
        return XQuery.getEntityList(ProductType.class, findAllSql);
    }

    @Override
    public ProductType findById(String id) {
        return XQuery.getSingleBean(ProductType.class, findByIdSql, id);
    }
    
}
