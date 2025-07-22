/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;
import quanli.ton.entity.BillDetails;

/**
 *
 * @author hieud
 */
public interface BillDetailDao extends CrudDAO<BillDetails, Long> {
    public List<BillDetails> findByBillId(Long billId);
    public boolean isBillDetailExisted(Long id);
}
