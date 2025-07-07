/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.controller;

import java.io.File;
import quanli.ton.entity.User;

/**
 *
 * @author PhuongTram
 */
public interface UserManagerController extends CrudController<User>{
    File chooseFile();
    String saveFile(File selectedFile);
}
