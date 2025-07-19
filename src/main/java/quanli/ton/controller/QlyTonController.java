/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.controller;

import quanli.ton.entity.Bills;

/**
 *
 * @author hieud
 */
public interface QlyTonController {
        void open(); // Xử lý mở cửa sổ
    
        void setForm(Bills entity); // Hiển thị thực thể lên form
        Bills getForm(); // Tạo thực thể từ dữ liệu form

        void fillToTable(); // Tải dữ liệu và đổ lên bảng
        void edit(); // Hiển thị dữ liệu của hàng được chọn lên form

        void create(); // Tạo thực thể mới
        void update(); // Cập nhật thực thể đang xem
        void delete(); // Xóa thực thể đang xem
        void clear();  // Xóa trắng form
        void setEditable(boolean editable); // Thay đổi trạng thái form

        void checkAll(); // Tích chọn tất cả các hàng trên bảng
        void uncheckAll(); // Bỏ tích chọn tất cả các hàng trên bảng
        void deleteCheckedItems(); // Xóa các thực thể được tích chọn 
        
        boolean isValidInput();
        
        void fillProductList();
        void fillTypeCbo();
        void fillThicknesCbo(String typeId);

        void fillBillDetail(long billId);
        void fillCustomer(String customerId);
}
