/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.Date;
import java.util.List;
import quanli.ton.dao.BillDao;
import quanli.ton.entity.Bills;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author hieud
 */
public class BillDaoImpl implements BillDao {

    String createSql = "INSERT INTO Bills(CustomerId, Username, Checkin, Checkout, Status) VALUES(?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Bills SET CustomerId=?, Username=?, Checkin=?, Checkout=?, Status=? WHERE Id=?";
    String deleteSql = "DELETE FROM Bills WHERE Id=?";
    String findAllSql = "SELECT * FROM Bills";
    String findByIdSql = "SELECT * FROM Bills WHERE Id=?";
    String findByTimeRangeSql = "SELECT * FROM Bills WHERE Checkin BETWEEN ? AND ? ORDER BY Checkin DESC";
    String findAllOfCustomerId = "SELECT * FROM Bills WHERE CustomerId = ?";
    String findNameByCustomer = "SELECT FullName FROM Customers WHERE PhoneNumber=?";

    @Override
    public Bills create(Bills entity) {
        Object[] values = {
            entity.getCustomerId(),
            entity.getUsername(),
            entity.getCheckin(),
            entity.getCheckout(),
            entity.getStatus()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Bills entity) {
        Object[] values = {
            entity.getCustomerId(),
            entity.getUsername(),
            entity.getCheckin(),
            entity.getCheckout(),
            entity.getStatus(),
            entity.getId()
        };
            XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Bills> findAll() {
        return XQuery.getBeanList(Bills.class, findAllSql);
    }

    @Override
    public Bills findById(Long id) {
        return XQuery.getSingleBean(Bills.class, findByIdSql, id);
    }

    @Override
    public List<Bills> findByTimeRange(Date begin, Date end) {
        return XQuery.getBeanList(Bills.class, findByTimeRangeSql, begin, end);
    }

    @Override
    public List<Bills> findAllOfCustomerId(String id) {
        return XQuery.getBeanList(Bills.class, findAllOfCustomerId, id);
    };
    
    @Override
    public String findNameByCustomerId(String CustomerId) {
        return XQuery.getSingleValue(findNameByCustomer, CustomerId);
    }
}
