package com.richard.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@Builder
@Setter
@Getter
@Entity
public class Voltage {

    @Id
    @Column (name = "ID", nullable = false)
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column (name = "volt", precision = 10, scale = 4, nullable = false)
    private BigDecimal volt;
    @NotNull
    @Column (name = "time", nullable = false)
    private double time;

    public Voltage(@NotNull BigDecimal volt, @NotNull double time) {
        this.volt = volt;
        this.time = time;
    }
}
