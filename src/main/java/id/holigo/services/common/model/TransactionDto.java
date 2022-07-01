package id.holigo.services.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    private UUID parentId;

    private String invoiceNumber;

    private Long userId;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    private Timestamp expiredAt;

    private BigDecimal fareAmount;

    private BigDecimal adminAmount;

    private BigDecimal discountAmount;

    private BigDecimal pointAmount;

    private PaymentStatusEnum paymentStatus;

    private OrderStatusEnum orderStatus;

    private String transactionId;

    private String transactionType;

    private Integer serviceId;

    private Integer productId;

    private String indexProduct;

    private UUID paymentId;

    private String paymentServiceId;

    private String voucherCode;

    private String indexUser;

    private String note;

    private BigDecimal ntaAmount;

    private BigDecimal nraAmount;

    private BigDecimal cpAmount;

    private BigDecimal mpAmount;

    private BigDecimal ipAmount;

    private BigDecimal hpAmount;

    private BigDecimal hvAmount;

    private BigDecimal prAmount;

    private BigDecimal ipcAmount;

    private BigDecimal hpcAmount;

    private BigDecimal prcAmount;

    private BigDecimal lossAmount;
}
