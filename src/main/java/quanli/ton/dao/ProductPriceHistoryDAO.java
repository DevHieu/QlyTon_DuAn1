/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.ProductPriceHistory;

/**
 *
 * @author huynhtrunghieu
 */
public interface ProductPriceHistoryDAO {
    List<ProductPriceHistory> findAllById(String id);
}
