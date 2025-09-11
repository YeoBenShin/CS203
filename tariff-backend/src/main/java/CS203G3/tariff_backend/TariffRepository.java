package CS203G3.tariff_backend;

import org.springframework.data.repository.CrudRepository;

import CS203G3.tariff_backend.TariffData;

public interface TariffRepository extends CrudRepository<TariffData, Integer> {
    
}
