/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.List;
import quanli.ton.dao.ProductPriceHistoryDAO;
import quanli.ton.entity.ProductPriceHistory;
import quanli.ton.util.XJdbc;
import quanli.ton.util.XQuery;

/**
 *
 * @author huynhtrunghieu
 */
public class ProductPriceHistoryDAOImpl implements ProductPriceHistoryDAO{

    String findAllById = "Select * from ProductPriceHistory where ProductId = ? ORDER BY EffectiveDate";
    
    @Override
    public List<ProductPriceHistory> findAllById(String id) {
      return  XQuery.getBeanList(ProductPriceHistory.class, findAllById, id);
    }
    
}
