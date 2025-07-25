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
public interface QlyTonController extends CrudController<Bills> {
    void fillProductList();
}
