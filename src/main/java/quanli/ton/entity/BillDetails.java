/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author hieud
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDetails {
    private long id;
    private long billId;
    private String productId;
    private double importPrice;
    private double unitPrice;
    private double discount;
    private double quantity;
    private double length;
    private Float defaultLength;
    private String productName;
}
