package com.ampuero.msvc.boleta.services;

import com.ampuero.msvc.boleta.clients.ClienteClientRest;
import com.ampuero.msvc.boleta.models.entities.Boleta;
import com.ampuero.msvc.boleta.repositories.BoletaRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


// Habilitamos la integración de Mockito con JUnit 5
@ExtendWith(MockitoExtension.class)
public class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ClienteClientRest clienteClientRest;

    @InjectMocks
    private BoletaServiceImpl boletaService;

    private List<Boleta> boletaList = new ArrayList<>();

    private Boleta boletaPrueba;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));
        for(int i=0;i<100;i++){
            Boleta boleta = new Boleta();
            boleta.setIdBoleta((long) i+1);
            boleta.setFechaEmisionBoleta(LocalDate.now().minusDays(faker.number().numberBetween(0, 90)));
            boleta.setTotalBoleta(faker.number().randomDouble(2, 1000, 50000));
            boleta.setDescripcionBoleta("Boleta por compra de " + faker.commerce().productName());
            boleta.setIdClientePojo((long) faker.number().numberBetween(1, 50));
            this.boletaList.add(boleta);
        }
        this.boletaPrueba = this.boletaList.get(0);
    }

    @Test
    @DisplayName("Debe crear una boleta")
    public void debeCrearBoleta() {

    }
}
