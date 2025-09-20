package CS203G3.tariff_backend.config;

import CS203G3.tariff_backend.model.Country;
import CS203G3.tariff_backend.model.Product;
import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.repository.CountryRepository;
import CS203G3.tariff_backend.repository.ProductRepository;
import CS203G3.tariff_backend.repository.TariffMappingRepository;
import CS203G3.tariff_backend.repository.TariffRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
        CountryRepository countryRepo,
        ProductRepository productRepo,
        TariffMappingRepository mappingRepo,
        TariffRepository tariffRepo
    ) {
        return args -> {
            // ✅ 1. Insert Countries
            if (countryRepo.count() == 0) {
                Country sgp = new Country();
                sgp.setIsoCode("SGP");
                sgp.setName("Singapore");
                sgp.setRegion("Asia");
                countryRepo.save(sgp);

                Country usa = new Country();
                usa.setIsoCode("USA");
                usa.setName("United States");
                usa.setRegion("North America");
                countryRepo.save(usa);
            
                // ✅ 2. Insert Product
                Product p1 = new Product();
                p1.setHsCode(1001);
                p1.setDescription("Wheat");
                productRepo.save(p1);

                // ✅ 3. Insert Tariff Mapping
                TariffMapping mapping = new TariffMapping();
                mapping.setExporter(sgp);
                mapping.setImporter(usa);
                mapping.setProduct(p1);
                mapping = mappingRepo.save(mapping); // save first to get ID

                // ✅ 4. Insert Tariff (linked to the mapping)
                Tariff tariff = new Tariff();
                tariff.setTariffMapping(mapping);
                tariff.setRate(BigDecimal.valueOf(5.1234));
                tariff.setEffectiveDate(Date.valueOf(LocalDate.of(2025,1,1)));
                tariff.setExpiryDate(Date.valueOf(LocalDate.of(2025,12,31)));
                tariff.setReference("Initial seed tariff");
                tariffRepo.save(tariff);

                System.out.println("✅ Seed data inserted successfully!");

                // to manually insert into user table
                // INSERT INTO users (uuid, is_admin) VALUES ('user_32jtRY55jxaWI2korGsZgHJM1S1', true);
            }
        };
    }
}
