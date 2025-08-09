/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.sql.ResultSet;
import java.util.List;
import quanli.ton.entity.Customer;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;
import quanli.ton.dao.CustomerDAO;

/**
 *
 * @author hieud
 */
public class CustomerDAOImpl implements CustomerDAO {

    String createSql = "INSERT INTO Customers(PhoneNumber, FullName, Address) VALUES(?, ?, ?)";
    String updateSql = "UPDATE Customers SET FullName=?,  Address=?  WHERE PhoneNumber=?";
    String deleteSql = "DELETE FROM Customers WHERE PhoneNumber=?";
    String findAllSql = "SELECT * FROM Customers";
    String findByIdSql = "SELECT * FROM Customers WHERE PhoneNumber=?";
    String isCustomerExistedSql = "SELECT 1 FROM Customers WHERE PhoneNumber=?";
    String findNameByCustomer = "SELECT FullName FROM Customers WHERE PhoneNumber=?";

    @Override
    public Customer create(Customer entity) {
        Object[] values = {
            entity.getPhoneNumber(),
            entity.getFullName(),
            entity.getAddress(),};
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Customer entity) {
        Object[] values = {
            entity.getFullName(),
            entity.getAddress(),
            entity.getPhoneNumber(),};
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Customer> findAll() {
        return XQuery.getBeanList(Customer.class, findAllSql);
    }

    @Override
    public Customer findById(String id) {
        return XQuery.getSingleBean(Customer.class, findByIdSql, id);
    }

    @Override
    public boolean isCustomerExisted(String phoneNumber) {
        try (
                ResultSet rs = XJdbc.executeQuery(isCustomerExistedSql, phoneNumber)) {
            return rs.next(); // nếu có dòng => tồn tại
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String findNameByCustomerId(String CustomerId) {
        return XQuery.getSingleValue(findNameByCustomer, CustomerId);
    }
}
