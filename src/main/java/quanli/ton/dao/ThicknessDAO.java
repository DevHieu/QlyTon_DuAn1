/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.Thickness;

/**
 *
 * @author USER
 */
public interface ThicknessDAO extends CrudDAO<Thickness, Integer>{
    List<Thickness> findByProductTypeId(String typeId);
}
