/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package quanli.ton.dao;

import java.util.List;

/**
 *
 * @author hieud
 * @param <T> kiểu dữ liệu cảu Object
 * @param <ID> kiểu dữ liệu của id
 */
public interface CrudDAO<T, ID> {
    T create(T entity);
    void update(T entity);
    void deleteById(ID id);
    List<T> findAll();
    T findById(ID id);
}
