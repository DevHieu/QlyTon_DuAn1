/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import quanli.ton.entity.Customer;

/**
 *
 * @author hieud
 */
public interface CustomerDao extends CrudDAO<Customer, String> {
    boolean isCustomerExisted(String phoneNumber);
}
