/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.entity.User;
import quanli.ton.util.XJdbc;
import quanli.ton.dao.UserDao;
import quanli.ton.util.XQuery;

/**
 *
 * @author Admin
 */
public class UserDAOImpl implements UserDao {

    String createSql = "INSERT INTO Users(Username, Password, Enabled, Fullname, Photo, Manager, PhoneNumber) VALUES(?, ?, ?, ?, ?, ?,?)";
    String updateSql = "UPDATE USERS SET Password = ?, Enabled = ?, Fullname = ?, Photo = ?, Manager = ?, PhoneNumber = ? WHERE Username = ?";
    String deleteSql = "DELETE FROM USERS WHERE Username = ?";
    String findAllSql = "SELECT * FROM USERS";
    String findByIdSql = "SELECT * FROM USERS WHERE Username = ?";

    @Override
    public User create(User entity) {
        Object[] values = {
            entity.getUsername(),
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.isManager(),
            entity.getPhoneNumber()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(User entity) {
        Object[] values = {
            entity.getPassword(),
            entity.isEnabled(),
            entity.getFullname(),
            entity.getPhoto(),
            entity.isManager(),
            entity.getPhoneNumber(),
            entity.getUsername()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<User> findAll() {
        return XQuery.getEntityList(User.class, findAllSql);
    }

    @Override
    public User findById(String id) {
        return XQuery.getSingleBean(User.class, findByIdSql, id);
    }

    @Override
    public boolean hasTransaction(String username) {
        String sql = "SELECT COUNT(*) FROM Bills WHERE Username = ?";
    try {
        var rs = XJdbc.executeQuery(sql, username);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        rs.getStatement().getConnection().close(); // đóng kết nối
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
    }
}
