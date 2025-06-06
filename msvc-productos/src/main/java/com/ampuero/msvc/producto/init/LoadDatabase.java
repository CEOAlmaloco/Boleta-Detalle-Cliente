package com.ampuero.msvc.producto.init;

import com.ampuero.msvc.producto.models.Producto;
import com.ampuero.msvc.producto.repositories.ProductoRepository;
//repository y la entidad
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Profile("dev")
@Component
public class LoadDatabase implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);


    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("es","CL"));

        if(productoRepository.count()==0){
            for(int i=0;i<1000;i++){
                Producto producto = new Producto();

                producto.setNombreProducto(faker.commerce().productName() + " " + faker.number().digits(3)); // "Laptop 123"
                producto.setDescripcionProducto(faker.lorem().sentence()); // Descripción realista
                producto.setPrecioProducto(faker.number().randomDouble(2, 10, 1000)); // Precio entre 10.00 y 1000.00

                logger.info("El nombre que agregas es {}", producto.getNombreProducto());
                producto = productoRepository.save(producto);
                logger.info("EL producto creado es: {}", producto);

            }
        }

    }
}
