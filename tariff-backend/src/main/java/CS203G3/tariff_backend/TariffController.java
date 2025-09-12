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
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
class TariffController {

    @Autowired
    private TariffRepository tariffRepository;

    @PostMapping(path="/tariff") // Map ONLY POST Requests
    public @ResponseBody String addNewTariff (@RequestBody TariffData tariffData) {
        tariffRepository.save(tariffData);
        return "Saved";
    }

    @GetMapping("/tariff")
    public @ResponseBody Iterable<TariffData> getAllTariff() {
        return tariffRepository.findAll();
    }

    // New endpoint for calculation
    @PostMapping(path="/tariff/calculate")
    public @ResponseBody CalculationResult calculateTariff(@RequestBody CalculationRequest req) {
        // Example calculation: totalCost = prodCost * quantity + (prodCost * quantity * rate)
        double prodCost = req.getProdCost();
        int quantity = req.getQuantity();
        double rate = req.getRate();
        double totalCost = prodCost * quantity * (1 + rate);
        CalculationResult result = new CalculationResult();
        result.setTotalCost(totalCost);
        return result;
    }
}

// DTO for calculation request
class CalculationRequest {
    private double prodCost;
    private int quantity;
    private double rate;
    // Add other fields as needed

    public double getProdCost() { return prodCost; }
    public void setProdCost(double prodCost) { this.prodCost = prodCost; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}

// DTO for calculation result
class CalculationResult {
    private double totalCost;
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
}
