package br.com.helio.springmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "beer_order_line")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "varchar(36)", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "order_quantity")
    private Integer orderQuantity;

    @Column(name = "quantity_allocated")
    private Integer quantityAllocated;

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    // Relationships:
    @ManyToOne
    private Beer beer;

    @ManyToOne
    private BeerOrder beerOrder;
}
