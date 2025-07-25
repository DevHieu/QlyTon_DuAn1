/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.entity;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author huynhtrunghieu
 */
@Data 
public class ProductPriceHistory {
    long id ;
    String productid;
    double importPrice;
    double unitPrice;
    Date effectiveDate;
}
