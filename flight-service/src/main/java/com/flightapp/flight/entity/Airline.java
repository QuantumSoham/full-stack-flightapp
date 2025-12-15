package com.flightapp.flight.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airline", uniqueConstraints = @UniqueConstraint(name = "uq_airline_code", columnNames = "code"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "flights")
@ToString(exclude = "flights")
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airline_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 10, unique = true)
    private String code;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true;

    @Version
    private Long version;
}
