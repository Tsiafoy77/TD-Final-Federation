package com.federation.agriculture.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CollectivityTransaction {
    private String id;
    private String collectivityId;
    private LocalDate creationDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String accountCreditedId;
    private FinancialAccount accountCredited;
    private String memberDebitedId;
    private Member memberDebited;
}