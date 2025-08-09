/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import quanli.ton.entity.User;

/**
 *
 * @author Admin
 */
public interface UserDAO extends CrudDAO<User, String> {
    boolean hasTransaction(String username);
}
