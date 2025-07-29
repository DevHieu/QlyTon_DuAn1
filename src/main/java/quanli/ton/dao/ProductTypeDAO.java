/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.ProductType;



/**
 *
 * @author USER
 */
public interface ProductTypeDAO extends CrudDAO<ProductType, String>{
    public String findNameById(String id);
    public List<ProductType> findHasThicknessTrue();
}
