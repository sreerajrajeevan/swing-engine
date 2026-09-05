package com.sree.swingengine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    private String isinCode;

    private String industry;

    @Column(nullable = false)
    private Boolean nifty500;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "symbol_token")
    private String symbolToken;
}