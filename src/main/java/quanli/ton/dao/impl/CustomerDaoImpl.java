/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.dao.CustomerDao;
import quanli.ton.entity.Customer;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author hieud
 */
public class CustomerDaoImpl implements CustomerDao {

    String createSql = "INSERT INTO KhachHang(Id, FullName, Sdt, DiaChi) VALUES(?, ?, ?, ?)";
    String updateSql = "UPDATE KhachHang SET FullName=?,  Sdt=?, DiaChi=?  WHERE Id=?";
    String deleteSql = "DELETE FROM KhachHang WHERE Id=?";
    String findAllSql = "SELECT * FROM KhachHang";
    String findByIdSql = "SELECT * FROM KhachHang WHERE Id=?";

    @Override
    public Customer create(Customer entity) {
        Object[] values = {
            entity.getId(),
            entity.getFullName(),
            entity.getSdt(),
            entity.getDiaChi(),};
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Customer entity) {
        Object[] values = {
            entity.getFullName(),
            entity.getSdt(),
            entity.getDiaChi(),
            entity.getId(),};
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(Long id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Customer> findAll() {
       return XQuery.getBeanList(Customer.class, findAllSql);
    }
    
    @Override
    public Customer findById(Long id) {
        return XQuery.getSingleBean(Customer.class, findByIdSql, id);
    }

}
