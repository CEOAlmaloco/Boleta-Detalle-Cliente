package com.ampuero.msvc.producto.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotNull(message = "El nombre del producto no puede estar vacio")
    @Column(nullable = false,unique = true)
    private String nombreProducto;

    @Column
    private String descripcionProducto;

    @NotNull(message = "El precio del producto no puede ser nulo ")
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    @Column
    private Double precioProducto;
}
