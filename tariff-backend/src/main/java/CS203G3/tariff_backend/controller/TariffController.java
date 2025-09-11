package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.service.TariffService;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


@RestController
public class TariffController {
    private TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("/tariffs")
    public List<Tariff> getTariffs() {
        return tariffService.listTariffs();
    }

    @GetMapping("/tariffs/{id}")
    public Tariff getTariff(Long tariffId) {
        return tariffService.getTariff(tariffId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tariffs")
    public Tariff addTariff(@RequestBody Tariff tariff) {
        return tariffService.addTariff(tariff);
    }

    @PutMapping ("/tariffs/{id}")
    public Tariff updateTariff(@PathVariable Long id, @RequestBody Tariff tariff) {
        return tariffService.updateTariff(id, tariff);
    }

    @DeleteMapping ("/tariffs/{id}")
    public Tariff deleteTariff(@PathVariable Long id) {
        return tariffService.deleteTariff(id);
    }


}
