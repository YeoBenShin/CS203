package CS203G3.tariff_backend.client;

import CS203G3.tariff_backend.model.Tariff;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class tariffClient {
    private RestTemplate restTemplate;

    public tariffClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get a tariff with given id
     *
     * @param URL
     * @param id
     * @return
     */
    public Tariff getTariff(final String URL, final String ProductID) {
        final Tariff tariff = restTemplate.getForObject(URL + "/" + ProductID, Tariff.class);
        return tariff;
    }

    /**
     * Add a new tariff
     *
     * @param URL
     * @param newTariff
     * @return
     */
    public Tariff addTariff(final String URL, final Tariff newTariff) {
        
        final Tariff tariff = restTemplate.postForObject(URL, newTariff, Tariff.class);
        return tariff;
    }

    public Tariff deleteTariff(final String URL, final String oldTariffID) {
        final Tariff tariff = restTemplate.getForObject(URL + "/" + oldTariffID, Tariff.class);
        restTemplate.delete(URL + "/" + oldTariffID);
        return tariff;
    }

    public Tariff updateTariff(final String URL, final String oldTariffID, final Tariff updatedTariff) {
        restTemplate.put(URL + "/" + oldTariffID, updatedTariff);
        final Tariff tariff = restTemplate.getForObject(URL + "/" + oldTariffID, Tariff.class);
        return tariff;
    }

}
