/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.controller;

import quanli.ton.entity.Product;

/**
 *
 * @author huynhtrunghieu
 */

public interface ProductsController extends CrudController<Product> {
     void fillBillDetails(); // tải và hiển thị chi tiết phiếu
    void selectTimeRange();
}
