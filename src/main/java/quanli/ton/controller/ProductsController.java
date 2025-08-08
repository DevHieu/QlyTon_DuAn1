/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.controller;

import java.io.File;
import quanli.ton.entity.Product;

/**
 *
 * @author huynhtrunghieu
 */
public interface ProductsController extends CrudController<Product> {

    File chooseFile();

    String saveFile(File selectedFile);
}
