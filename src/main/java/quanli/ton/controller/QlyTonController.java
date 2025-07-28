/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.controller;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.util.List;
import quanli.ton.entity.BillDetails;
import quanli.ton.entity.Bills;
import quanli.ton.entity.Customer;
import quanli.ton.util.XDialog;

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
        
        void fillProductList(boolean isType, boolean isThickness);
        void fillTypeCbo();
        void fillThicknesCbo(String typeId);
        
        void fillBillsToTable(List<Bills> billsList);
        void fillTimeRange();
        void selectTimeRange();
        void fillTableBySearch();

        default void exit(){
        if(XDialog.confirm("Bạn muốn kết thúc?")){
            System.exit(0);
        }
    }
    
//    default void showJDialog(JDialog dialog){
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//    }
//    
//    default boolean showWelcomeJDialog(JFrame frame){
//        WelcomeJDialog dialog = new WelcomeJDialog(frame, true);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//        return dialog.isProceed();
//    }
//
//    default boolean showLoginJDialog(JFrame frame){
//        LoginJDialog dialog = new LoginJDialog(frame, true);
//        dialog.setLocationRelativeTo(null);
//        dialog.setVisible(true);
//        return dialog.isProceed();
//    }
//    
//    default void showChangePasswordJDialog(JFrame frame){
//        this.showJDialog(new ChangePasswordJDialog(frame, true));
//    }
//    
//    default void showSalesJDialog(JFrame frame){
//        this.showJDialog(new SalesJDialog(frame, true));
//    }
//    
//    default void showHistoryJDialog(JFrame frame){
//        this.showJDialog(new HistoryJDialog(frame, true));
//    }
//    
//    default void showDrinkManagerJDialog(JFrame frame){
//        this.showJDialog(new DrinkManagerJDialog(frame, true));
//    }
//    
//    default void showCategoryManagerJDialog(JFrame frame){
//        this.showJDialog(new CategoryManagerJDialog(frame, true));
//    }
//    
//    default void showCardManagerJDialog(JFrame frame){
//        this.showJDialog(new CardManagerJDialog(frame, true));
//    }
//    
//    default void showBillManagerJDialog(JFrame frame){
//        this.showJDialog(new BillManagerJDialog(frame, true));
//    }
//    
//    default void showUserManagerJDialog(JFrame frame){
//        this.showJDialog(new UserManagerJDialog(frame, true));
//    }
//    
//    default void showRevenueManagerJDialog(JFrame frame){
//        this.showJDialog(new RevenueManagerJDialog(frame, true));
//    }
}
