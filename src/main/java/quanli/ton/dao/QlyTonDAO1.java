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
public interface QlyTonDAO1 extends CrudDAO<Bills, Long>{
        public List<Bills> findByTimeRange(Date begin, Date end);
}
