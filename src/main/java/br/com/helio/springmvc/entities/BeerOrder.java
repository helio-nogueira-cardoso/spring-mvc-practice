package br.com.helio.springmvc.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "beer_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(name = "customer_ref")
    @Size(max = 255)
    private String customerRef;

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
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder")
    @Builder.Default
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();
}
