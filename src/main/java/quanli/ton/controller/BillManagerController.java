/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.controller;

import quanli.ton.entity.Bills;
import quanli.ton.entity.Customer;

/**
 *
 * @author hieud
 */
public interface BillManagerController extends CrudController<Bills> {

    void fillBillDetails(); // tải và hiển thị chi tiết phiếu

    void selectTimeRange(); // xử lý chọn khoảng thời gian trong cboTimeRanges
    
     void setForm(Bills bills, Customer customer);
     
     Customer findCustomer(String phoneNumber);
     
     Customer getCustomerForm();
}
