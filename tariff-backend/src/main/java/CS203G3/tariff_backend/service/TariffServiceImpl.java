package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.repository.TariffRepository;
import jakarta.transaction.Transactional;
import CS203G3.tariff_backend.exception.TariffNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of TariffService with business logic
 * Uses exceptions for cleaner error handling
 */
@Service
public class TariffServiceImpl implements TariffService {

    @Autowired
    private TariffRepository tariffRepository;

    @Override
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    @Override
    public Tariff getTariffById(Long id) throws TariffNotFoundException {
        return tariffRepository.findById(id)
            .orElseThrow(() -> new TariffNotFoundException(id));
    }

    @Override
    @Transactional
    public Tariff createTariff(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public Tariff updateTariff(Long id, Tariff tariff) throws TariffNotFoundException {
        // Check if tariff exists
        if (!tariffRepository.existsById(id)) {
            throw new TariffNotFoundException(id);
        }
        // Set the ID to ensure we update the correct record
        tariff.setTariffID(id);
        return tariffRepository.save(tariff);
    }

    @Override
    @Transactional
    public void deleteTariff(Long id) throws TariffNotFoundException {
        if (!tariffRepository.existsById(id)) {
            throw new TariffNotFoundException(id);
        }
        
        tariffRepository.deleteById(id);
    }

    @Override
    public List<Tariff> getTariffsByMappingId(Long tariffMappingId) {
        // This would require a custom query in repository
        // For now, filter in service (not efficient for large datasets)
        return tariffRepository.findAll().stream()
                .filter(tariff -> tariff.getTariffMappingID().equals(tariffMappingId))
                .toList();
    }

    @Override
    public double calculateTotalCost(double productCost, int quantity, double tariffRate) {
        double totalProductCost = productCost * quantity;
        double totalTariff = totalProductCost * tariffRate;
        return totalProductCost + totalTariff;
    }
}
