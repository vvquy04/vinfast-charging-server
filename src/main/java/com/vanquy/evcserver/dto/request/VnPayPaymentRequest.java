package com.vanquy.evcserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VnPayPaymentRequest {
    private Long amount; // Số tiền nạp ví, đơn vị VND
}
