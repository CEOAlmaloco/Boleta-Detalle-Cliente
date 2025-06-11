package com.ampuero.msvc.producto.models;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa un producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    @Schema(description = "primary key de producto", examples = "1")
    private Long idProducto;

    @NotNull(message = "El nombre del producto no puede estar vacio")
    @Column(nullable = false,unique = true)
    @Schema(description = "nombre del producto es obligario", examples = "playstation")
    private String nombreProducto;

    @Column
    private String descripcionProducto;

    @NotNull(message = "El precio del producto no puede ser nulo ")
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    @Column
    @Schema(description = "precio del producto debe ser double", examples = "2.0")
    private Double precioProducto;
}
