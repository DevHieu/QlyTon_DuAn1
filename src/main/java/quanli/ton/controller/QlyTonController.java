/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.controller;

import java.util.List;
import quanli.ton.entity.BillDetails;
import quanli.ton.entity.Bills;
import quanli.ton.entity.Customer;

/**
 *
 * @author hieud
 */
public interface QlyTonController {
        void open(); // Xử lý mở cửa sổ
    
        void fillBill(Bills entity);
        void fillBillDetail();
        void fillCustomer(String customerId);
        
        Bills getBillsForm();
        Customer getCustomerForm();
        
        void fillToTable(); 
        void save(); // Cập nhật thực thể đang xem
        void cancle(); // Xóa thực thể đang xem
        void print();
        void clear();  // Xóa trắng form
        void setEditable(int status); // Thay đổi trạng thái form

        boolean isValidInput();
        
        void fillProductList();
        void fillTypeCbo();
        void fillThicknesCbo(String typeId);
        
        void fillBillsToTable(List<Bills> billsList);
        void fillTimeRange();
        void selectTimeRange();
        void fillTableBySearch();

}
