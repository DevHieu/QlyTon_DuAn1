/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import quanli.ton.dao.BillDao;
import quanli.ton.entity.Bills;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author hieud
 */
public class BillDaoImpl implements BillDao {
    String createSql = "INSERT INTO Bills(CustomerId, Username, Checkin, Checkout, Note, Discount, Deposit, Status) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    String updateSql = "UPDATE Bills SET CustomerId=?, Username=?, Checkin=?, Checkout=?, Note=?, Discount=?, Deposit=?, Status=? WHERE Id=?";
    String deleteSql = "DELETE FROM Bills WHERE Id=?";
    String findAllSql = "SELECT * FROM Bills";
    String findByIdSql = "SELECT * FROM Bills WHERE Id=?";
    String findByTimeRangeSql = "SELECT * FROM Bills WHERE Checkin BETWEEN ? AND ? ORDER BY Checkin DESC";
    String findAllOfCustomerId = "SELECT * FROM Bills WHERE CustomerId = ?";
  
    String findNameByCustomer = "SELECT FullName FROM Customers WHERE PhoneNumber=?";
    // SQL mới cho selectByTimeRange để lấy thông tin chi tiết hơn
    String selectByTimeRangeSql = "SELECT b.Id, b.Username, c.FullName, b.Checkin, b.Checkout, b.Status FROM Bills b JOIN Customers c ON b.CustomerId = c.PhoneNumber WHERE b.Checkin BETWEEN ? AND ? ORDER BY b.Checkin DESC"; //
    // SQL mới cho selectBillDetails để lấy thông tin sản phẩm và chi tiết hóa đơn
    String selectBillDetailsSql = "SELECT p.Name, bd.Quantity, bd.UnitPrice, bd.Length, bd.Discount, (bd.Quantity * bd.UnitPrice * (1 - bd.Discount)) AS Total FROM BillDetails bd JOIN Products p ON bd.ProductId = p.Id WHERE bd.BillId = ?"; //

    // Tìm các đơn đang ở trạng thái "đang xử lý"
    String findOperatingByIdSql = "SELECT * FROM Bills WHERE Id=? AND Status = 0";
    String findOperatingByTimeRangeSql = "SELECT * FROM Bills WHERE Status = 0 AND Checkin BETWEEN ? AND ? ORDER BY Checkin DESC";
    String findOperatingAllOfCustomerId = "SELECT * FROM Bills WHERE Status = 0 AND PhoneNumber = ?";

    @Override
    public Bills create(Bills entity) {
        Connection connection = XJdbc.openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)) { //Statement trả về key khi tạo xong bills
            stmt.setObject(1, entity.getCustomerId());
            stmt.setObject(2, entity.getUsername());
            stmt.setObject(3, entity.getCheckin());
            stmt.setObject(4, entity.getCheckout());
            stmt.setObject(5, entity.getNote());
            stmt.setObject(6, entity.getDiscount());
            stmt.setObject(7, entity.getDeposit());
            stmt.setObject(8, entity.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy id của bill vừa tạo
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long billId = generatedKeys.getLong(1);
                        // Bây giờ bạn có ID để tìm bill
                        entity.setId(billId);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(BillDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entity;
    }

    @Override
    public void update(Bills entity) {
        Object[] values = {
            entity.getCustomerId(),
            entity.getUsername(),
            entity.getCheckin(),
            entity.getCheckout(),
            entity.getNote(),
            entity.getDiscount(),
            entity.getDeposit(),
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
    public Bills findOperatingById(Long id) {
        return XQuery.getSingleBean(Bills.class, findOperatingByIdSql, id);
    }

    @Override
    public List<Bills> findOperatingByTimeRange(Date begin, Date end) {
        return XQuery.getBeanList(Bills.class, findOperatingByTimeRangeSql, begin, end);
    }

    @Override
    public List<Object[]> selectByTimeRange(Date begin, Date end) {
        List<Object[]> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(selectByTimeRangeSql, begin, end);
            while (rs.next()) {
                Object[] row = {
                    rs.getLong("Id"),
                    rs.getString("Username"),
                    rs.getString("FullName"),
                    rs.getTimestamp("Checkin"),
                    rs.getTimestamp("Checkout"),
                    rs.getInt("Status") == 1 ? "Hoàn thành" : (rs.getInt("Status") == 0 ? "Đang xử lý" : "Đã hủy")
                };
                list.add(row);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Object[]> selectBillDetails(Long billId) {
        List<Object[]> list = new ArrayList<>();
        try {
            ResultSet rs = XJdbc.executeQuery(selectBillDetailsSql, billId);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("Name"),
                    rs.getInt("Quantity"),
                    rs.getDouble("UnitPrice"),
                    rs.getObject("Length"), // Length có thể null
                    rs.getDouble("Discount"),
                    rs.getDouble("Total")
                };
                list.add(row);
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
        
     }
} 


    @Override
    public List<Bills> findOperatingAllOfCustomerId(String id) {
        return XQuery.getBeanList(Bills.class, findOperatingAllOfCustomerId, id);
    };

}

