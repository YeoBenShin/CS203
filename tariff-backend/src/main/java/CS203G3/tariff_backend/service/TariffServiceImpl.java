package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.exception.TariffNotFoundException;
import CS203G3.tariff_backend.model.Tariff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TariffServiceImpl implements TariffService {
    private ArrayList<Tariff> tariffs = new ArrayList<>();

    @Override
    public List<Tariff> listTariffs() {
        return tariffs;
    }

    @Override
    public Tariff getTariff(Long id) {
        for (Tariff tariff : tariffs) {
            if (tariff.getTariffID() == id) {
                return tariff;
            }
        }
        throw  new TariffNotFoundException(id);
    }

    @Override
    public Tariff addTariff(Tariff tariff) {
        tariffs.add(tariff);
        return tariff;
    }

    @Override
    public Tariff updateTariff(Long id, Tariff tariff) {
        for (Tariff t : tariffs) {
            if (t.getTariffID().equals(id)) {
                t.setTariffRate(tariff.getTariffRate());
                t.setProductID(tariff.getProductID());
                t.setExporter(tariff.getExporter());
                t.setImporter(tariff.getImporter());
                t.setEffectiveDate(tariff.getEffectiveDate());
                t.setExpiryDate(tariff.getExpiryDate());
                return tariff;
            }
        }
        throw new TariffNotFoundException(id);
    }

    @Override
    public Tariff deleteTariff(Long id) {
        Iterator<Tariff> it = tariffs.iterator();

        while (it.hasNext()) {
            Tariff tariff = it.next();
            if (tariff.getTariffID().equals(id)) {
                it.remove();
                return tariff;
            }
        }

        throw new TariffNotFoundException(id);
    }
}
