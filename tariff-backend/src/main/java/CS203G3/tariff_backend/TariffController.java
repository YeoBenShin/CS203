package CS203G3.tariff_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
class TariffController {

    @Autowired
    private TariffRepository tariffRepository;

    @PostMapping(path="/tariff") // Map ONLY POST Requests
	public @ResponseBody String addNewTariff (@RequestBody TariffData tariffData) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		tariffRepository.save(tariffData);
		return "Saved";
	}

    @GetMapping("/tariff")
    public @ResponseBody Iterable<TariffData> getAllTariff() {
        return tariffRepository.findAll();
    }
    
}
