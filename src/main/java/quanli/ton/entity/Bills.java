/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package quanli.ton.entity;

import java.util.Date;
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
public class Bills {

    private long id;
    private String username;
    @Builder.Default
    private Date checkin = new Date();
    private Date checkout;
    private int status;
    private Long customerId;

    public static enum Status {
        PROCESSING,
        COMPLETED,
        CANCELED
    }
}
