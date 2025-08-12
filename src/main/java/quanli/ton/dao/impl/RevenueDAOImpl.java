/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.dao.impl;

import java.util.Date;
import java.util.List;
import quanli.ton.dao.RevenueDAO;
import quanli.ton.entity.Revenue;
import quanli.ton.util.XQuery;

/**
 *
 * @author Admin
 */
public class RevenueDAOImpl implements RevenueDAO {

    @Override
    public List<Revenue.ByCategory> getByCategory(Date begin, Date end) {
        String revenueByCategorySql = "{CALL sp_GetRevenueByCategory(?, ?)}";
        return XQuery.getBeanList(Revenue.ByCategory.class, revenueByCategorySql, begin, end);
    }

    @Override
    public List<Revenue.ByUser> getByUser(Date begin, Date end) {
        String revenueByUserSql = "{CALL sp_GetRevenueByUser(?, ?)}";
        return XQuery.getBeanList(Revenue.ByUser.class, revenueByUserSql, begin, end);
    }
}
