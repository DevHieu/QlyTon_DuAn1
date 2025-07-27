/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import quanli.ton.dao.BillDetailDao;
import quanli.ton.entity.BillDetails;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author hieud
 */
public class BillDetailDAOImpl implements BillDetailDao {
    // Thêm ImportPrice vào INSERT
    String createSql = "INSERT INTO BillDetails(BillId, ProductId, UnitPrice, ImportPrice, Discount, Quantity, Length) VALUES(?, ?, ?, ?, ?, ?, ?)";
    
    // Thêm ImportPrice vào UPDATE
    String updateSql = "UPDATE BillDetails SET BillId=?, ProductId=?, UnitPrice=?, ImportPrice=?, Discount=?, Quantity=?, Length=? WHERE Id=?";
    
    String deleteSql = "DELETE FROM BillDetails WHERE Id=?";
    
    String findAllSql = "SELECT bd.*, p.name AS productName FROM BillDetails bd JOIN Products p ON p.Id=bd.ProductId";
    
    String findByIdSql
            = "SELECT bd.*, p.name AS productName, pt.DefaultLength "
            + "FROM BillDetails bd "
            + "JOIN Products p ON p.Id = bd.ProductId "
            + "JOIN ProductType pt ON pt.Id = p.TypeId "
            + "WHERE bd.Id = ?";
    
    String findByBillIdSql
            = "SELECT bd.*, p.name AS productName, pt.DefaultLength "
            + "FROM BillDetails bd "
            + "JOIN Products p ON p.Id = bd.ProductId "
            + "JOIN ProductType pt ON pt.Id = p.TypeId "
            + "WHERE bd.BillId = ?";
    
    // Sửa query này - SELECT TRUE thay vì True
    String isBillDetailExistedSql = "SELECT 1 FROM BillDetails WHERE Id = ?";
    
    @Override
    public List<BillDetails> findByBillId(Long billId) {
        return XQuery.getBeanList(BillDetails.class, findByBillIdSql, billId);
    }
    
    @Override
    public BillDetails create(BillDetails entity) {
        Object[] values = {
            entity.getBillId(),
            entity.getProductId(),
            entity.getUnitPrice(),
            entity.getImportPrice(),  // Thêm ImportPrice
            entity.getDiscount(),
            entity.getQuantity(),
            entity.getLength()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }
    
    @Override
    public void update(BillDetails entity) {
        Object[] values = {
            entity.getBillId(),
            entity.getProductId(),
            entity.getUnitPrice(),
            entity.getImportPrice(),  // Thêm ImportPrice
            entity.getDiscount(),
            entity.getQuantity(),
            entity.getLength(),
            entity.getId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }
    
    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }
    
    @Override
    public List<BillDetails> findAll() {
        return XQuery.getBeanList(BillDetails.class, findAllSql);
    }
    
    @Override
    public BillDetails findById(Long id) {
        return XQuery.getSingleBean(BillDetails.class, findByIdSql, id);
    }
    
    @Override
    public boolean isBillDetailExisted(Long id){
        ResultSet rs = XJdbc.executeQuery(isBillDetailExistedSql, id);
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(BillDetailDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}