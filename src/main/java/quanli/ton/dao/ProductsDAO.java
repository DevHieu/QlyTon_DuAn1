/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.Product;

/**
 *
 * @author huynhtrunghieu
 */
public interface ProductsDAO extends CrudDAO<Product, String> {

    List<Product> findProductByName(String textInput);

    List<Product> findProductByType(String id);

    List<Product> findProductByThick(int id);
}
