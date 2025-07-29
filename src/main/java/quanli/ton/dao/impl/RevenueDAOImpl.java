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
        String revenueByCategorySql
                = "SELECT productType.Name AS Category, "
                + "   SUM(detail.UnitPrice * detail.Quantity * (1 - detail.Discount/100.0)) AS Revenue, "
                + "   CAST(SUM(detail.Quantity) AS SIGNED) AS Quantity, "
                + "   MIN(detail.UnitPrice) AS MinPrice, "
                + "   MAX(detail.UnitPrice) AS MaxPrice, "
                + "   AVG(detail.UnitPrice) AS AvgPrice, "
                + "   SUM(detail.ImportPrice * detail.Quantity) AS Cost " 
                + "FROM BillDetails detail "
                + "   JOIN Products product ON product.Id = detail.ProductId "
                + "   JOIN ProductType productType ON productType.Id = product.TypeId "
                + "   JOIN Bills bill ON bill.Id = detail.BillId "
                + "WHERE bill.Status = 1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "GROUP BY productType.Name "
                + "ORDER BY Revenue DESC";
        return XQuery.getBeanList(Revenue.ByCategory.class, revenueByCategorySql, begin, end);
    }

    @Override
    public List<Revenue.ByUser> getByUser(Date begin, Date end) {
        String revenueByUserSql
                = "SELECT bill.Username AS `User`, "
                + "   SUM(detail.UnitPrice * detail.Quantity * (1 - detail.Discount/100.0)) AS Revenue, "
                + "   CAST(COUNT(DISTINCT detail.BillId) AS SIGNED) AS Quantity, "
                + "   MIN(bill.Checkin) AS FirstTime, "
                + "   MAX(bill.Checkin) AS LastTime "
                + "FROM BillDetails detail "
                + "   JOIN Bills bill ON bill.Id = detail.BillId "
                + "WHERE bill.Status = 1 "
                + "   AND bill.Checkout IS NOT NULL "
                + "   AND bill.Checkout BETWEEN ? AND ? "
                + "GROUP BY bill.Username "
                + "ORDER BY Revenue DESC";
        return XQuery.getBeanList(Revenue.ByUser.class, revenueByUserSql, begin, end);
    }
}
