/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.Product;
import quanli.ton.ui.manager.Products;
/**
 *
 * @author huynhtrunghieu
 */
public interface ProductsDAO {
    void insert(Product product);
    void update(Product product);
    void delete(String id);
    Products findById(String id);
    List<Products> findAll();
    public List<Product> selectAll();
}
