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
import quanli.ton.ui.ChangePasswordJDialog;
import quanli.ton.ui.HistoryJDialog;
import quanli.ton.ui.LoginJDialog;
import quanli.ton.ui.WelcomeJDialog;
import quanli.ton.ui.manager.BillManagerJDialog;
import quanli.ton.ui.manager.CustomerManagerJDialog;
import quanli.ton.ui.manager.ProductManagerJDialog;
import quanli.ton.ui.manager.ProductTypeManagerJDialog;
import quanli.ton.ui.manager.RevenueManager;
import quanli.ton.ui.manager.ThicknessManagerJDialog;
import quanli.ton.ui.manager.UserManagerJDialog;
import quanli.ton.util.XDialog;

/**
 *
 * @author hieud
 */
public interface QlyTonController {

    void init();

    void open(); // Xử lý mở cửa sổ

    void fillBill(Bills entity);

    void fillBillDetail();

    void fillCustomer(String customerId);

    Bills getBillsForm();

    Customer getCustomerForm();

    boolean save(); // Cập nhật thực thể đang xem

    void cancle(); // Xóa thực thể đang xem

    void print();

    void clear();  // Xóa trắng form

    void setEditable(int status); // Thay đổi trạng thái form

    boolean isValidInput();

    void fillProductList(String typeId, Integer thickId);

    void fillTypeCbo();

    void fillThicknesCbo(String typeId);

    void fillBillsToTable();

    void selectTimeRange();

    void fillBillBySearch();
    
    void logout();

    default void exit() {
        if (XDialog.confirm("Bạn muốn kết thúc?")) {
            System.exit(0);
        }
    }

    default void showJDialog(JDialog dialog) {
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    default boolean showWelcomeJDialog(JFrame frame){
        WelcomeJDialog dialog = new WelcomeJDialog(frame, true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.isProceed();
    }
    
    default boolean showLoginJDialog(JFrame frame){
        LoginJDialog dialog = new LoginJDialog(frame, true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.isProceed();
    }
    
    default void showChangePasswordJDialog(JFrame frame) {
        this.showJDialog(new ChangePasswordJDialog(frame, true));
    }

    default void showHistoryJDialog(JFrame frame) {
        this.showJDialog(new HistoryJDialog(frame, true));
    }

    default void showRevenueManagerJDialog(JFrame frame) {
        this.showJDialog(new RevenueManager(frame, true));
    }

    default void showUserManagerJDialog(JFrame frame) {
        this.showJDialog(new UserManagerJDialog(frame, true));
    }

    default void showBillManagerJDialog(JFrame frame) {
        this.showJDialog(new BillManagerJDialog(frame, true));
    }

    default void showProductManagerJDialog(JFrame frame) {
        this.showJDialog(new ProductManagerJDialog(frame, true));
    }

    default void showProductTypeManagerJDialog(JFrame frame) {
        this.showJDialog(new ProductTypeManagerJDialog(frame, true));
    }

    default void showThicknessManagerJDialog(JFrame frame) {
        this.showJDialog(new ThicknessManagerJDialog(frame, true));
    }

    default void showCustomerManagerJDialog(JFrame frame) {
        this.showJDialog(new CustomerManagerJDialog(frame, true));
    }
}
