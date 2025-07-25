/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.controller;

/**
 *
 * @author huynhtrunghieu
 */
public interface ImportgoodsController {
    boolean handleImportProduct(String productId, int quantityToAdd, double newUnitPrice) throws Exception;
}
