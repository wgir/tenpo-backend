package com.tenpo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "transactions_transaction_id_seq", allocationSize = 1)
    @Column(name = "transaction_id")
    private Integer id;

    @Column(name = "transaction_amount", nullable = false)
    private Integer amount;

    @Column(name = "merchant_or_business", nullable = false)
    private String merchantOrBusiness;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
