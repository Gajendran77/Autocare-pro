package com.autocarepro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 60)
    private String category; // ENGINE, ELECTRICAL, BODY, TYRE, etc.

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(length = 30)
    @Builder.Default
    private String status = "IN_STOCK"; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}
