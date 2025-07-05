/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.dao.ThicknessDAO;
import quanli.ton.entity.ProductType;
import quanli.ton.entity.Thickness;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author USER
 */
public class ThicknessDAOImpl implements ThicknessDAO{
    String createSql = "INSERT INTO Thickness(Id, Thick, TypeId) VALUES(?, ?, ?)";
    String updateSql = "UPDATE Thickness SET Thick=?, TypeId=? WHERE Id=?";
    String deleteSql = "DELETE FROM Thickness WHERE Id=?";
    String findAllSql = "SELECT * FROM Thickness";
    String findByIdSql = "SELECT * FROM Thickness WHERE Id=?";
    String findByTypeIdSql = "SELECT * FROM Thickness WHERE TypeId=?";
    
    @Override
    public Thickness create(Thickness entity) {
        Object[] values = {
            entity.getId(),
            entity.getThick(),
            entity.getTypeId()
        };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Thickness entity) {
        Object[] values = {
            entity.getId(),
            entity.getThick(),
            entity.getTypeId()
        };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(Integer id) {
        XJdbc.executeUpdate(deleteSql, id);
    }

    @Override
    public List<Thickness> findAll() {
        return XQuery.getEntityList(Thickness.class, findAllSql);
    }

    @Override
    public Thickness findById(Integer id) {
        return XQuery.getSingleBean(Thickness.class, findByIdSql, id);
    }

    @Override
    public List<Thickness> findByProductTypeId(String typeId) {
        return XQuery.getBeanList(Thickness.class, findByTypeIdSql, typeId);
    }
    
}
