/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import java.util.Date;
import java.util.List;
import quanli.ton.entity.Bills;

/**
 *
 * @author hieud
 */
public interface BillDAO1 extends CrudDAO<Bills, Long> {

    public List<Bills> findByTimeRange(Date begin, Date end);
    List<Object[]> findByUserAndTimeRange(String username, Date begin, Date end);

    public List<Bills> findAllOfCustomerId(String id);

    public String findNameByCustomerId(String CustomerId); 
    List<Object[]> selectByTimeRange(Date begin, Date end);
    List<Object[]> selectBillDetails(Long billId);

    public Bills findOperatingById(Long id);
    public List<Bills> findOperatingByTimeRange(Date begin, Date end);
    public List<Bills> findOperatingAllOfCustomerId(String id);
    
    public void cancleBill(long id);
}
