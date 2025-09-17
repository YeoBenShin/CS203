package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.repository.TariffMappingRepository;
import CS203G3.tariff_backend.exception.TariffMappingNotFoundException;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TariffMappingServiceImpl implements TariffMappingService {
    @Autowired
    private final TariffMappingRepository tariffMappingRepository;

    
    public TariffMappingServiceImpl(TariffMappingRepository tariffMappingRepository) {
        this.tariffMappingRepository = tariffMappingRepository;
    }

    @Override
    @Transactional
    public TariffMapping createTariffMapping(TariffMapping tariffMapping) {
        return tariffMappingRepository.save(tariffMapping);        
    }

    @Override
    @Transactional
    public void deleteTariffMapping(Long id) throws TariffMappingNotFoundException {
        if (!tariffMappingRepository.existsById(id)) {
            throw new TariffMappingNotFoundException(id);
        }
        
        tariffMappingRepository.deleteById(id);
    }

    @Override
    public List<TariffMapping> getAllTariffMappings() {
        return tariffMappingRepository.findAll();
    }

    @Override
    public TariffMapping getTariffMappingById(Long id) throws TariffMappingNotFoundException {
        return tariffMappingRepository.findById(id).orElseThrow(() -> new TariffMappingNotFoundException(id));
    }

    @Override
    @Transactional
    public TariffMapping updateTariffMapping(Long id, TariffMapping tariffMapping)
            throws TariffMappingNotFoundException {
        if (!tariffMappingRepository.existsById(id)) {
            throw new TariffMappingNotFoundException(id);
        }

        tariffMapping.setTariffMappingID(id);
        return tariffMappingRepository.save(tariffMapping);
    }
  
}
