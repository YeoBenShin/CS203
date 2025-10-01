package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.dto.TariffForCalDisplayDto;
import CS203G3.tariff_backend.dto.WatchlistCreationDto;
import CS203G3.tariff_backend.dto.WatchlistRequestDto;
import CS203G3.tariff_backend.exception.ResourceAlreadyExistsException;
import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.model.TariffMapping;
import CS203G3.tariff_backend.model.User;
import CS203G3.tariff_backend.model.Watchlist;
import CS203G3.tariff_backend.repository.TariffRepository;
import CS203G3.tariff_backend.repository.UserRepository;
import CS203G3.tariff_backend.repository.WatchlistRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private WatchlistRepository watchlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    public WatchlistServiceImpl(WatchlistRepository watchlistRepository, UserRepository userRepository, TariffRepository tariffRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.tariffRepository = tariffRepository;
    }

    private WatchlistRequestDto convertToDto(Watchlist watchlist) {
        WatchlistRequestDto dto = new WatchlistRequestDto();
        Tariff tariff = watchlist.getTariff();
        dto.setTariffID(tariff.getTariffID());
        dto.setTariffMappingID(tariff.getTariffMapping().getTariffMappingID());
        dto.setRate(tariff.getRate());
        dto.setEffectiveDate(tariff.getEffectiveDate());
        dto.setExpiryDate(tariff.getExpiryDate());
        dto.setReference(tariff.getReference());
        
        // Add mapping details for frontend display
        TariffMapping mapping = tariff.getTariffMapping();
        dto.setExporterCode(mapping.getExporter().getIsoCode());
        dto.setExporterName(mapping.getExporter().getName());
        dto.setImporterCode(mapping.getImporter().getIsoCode());
        dto.setImporterName(mapping.getImporter().getName());
        dto.setHSCode(mapping.getProduct().getHsCode());
        dto.setProductDescription(mapping.getProduct().getDescription());

        dto.setWatchlistID(watchlist.getWatchlistID());
        dto.setUserID(watchlist.getUser().getUuid());
        
        return dto;
    }

    public List<WatchlistRequestDto> getAllWatchlists() {
        List<Watchlist> watchlists = watchlistRepository.findAll();
        return watchlists.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Watchlist> createWatchlist(WatchlistCreationDto watchlist) {
        List<Watchlist> watchlistsToCreate = new ArrayList<>();
        for (TariffForCalDisplayDto tariff : watchlist.getTariffs()) {
            Watchlist newWatchlist = new Watchlist(userRepository.findByUuid(watchlist.getUuid()), tariffRepository.findById(tariff.getTariffID()).orElse(null));
            watchlistsToCreate.add(newWatchlist);
        }

        List<Watchlist> addedToWatchlists = new ArrayList<>();
        for (Watchlist wl : watchlistsToCreate) {
            Watchlist existing = watchlistRepository.findByTariff(wl.getTariff());
            System.out.println(existing);
            if (existing == null) {
                watchlistRepository.save(wl);
                addedToWatchlists.add(wl);
            } else {
                System.out.println("executed");
                throw new ResourceAlreadyExistsException("Tariff is already in the watchlist");
            }
        }

        return addedToWatchlists;
    }
}
