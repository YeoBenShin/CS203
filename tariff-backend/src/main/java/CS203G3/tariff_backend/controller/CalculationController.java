// package CS203G3.tariff_backend.controller;

// import CS203G3.tariff_backend.dto.CalculationRequest;
// import CS203G3.tariff_backend.dto.CalculationResult;
// import CS203G3.tariff_backend.service.CalculationServiceImpl;
// import CS203G3.tariff_backend.exception.MissingFieldException;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// /**
//  * REST Controller for tariff calculation endpoints
//  */
// @RestController
// @RequestMapping("/api/calculations")
// public class CalculationController {

//     private final CalculationServiceImpl calculationService;

//     public CalculationController(CalculationServiceImpl calculationService) {
//         this.calculationService = calculationService;
//     }

//     @PostMapping
//     public ResponseEntity<CalculationResult> calculateTariff(@RequestBody CalculationRequest input) {
//         if (input.getShippingCost() == null || input.gethsCode() == 0 || input.getCountry() == null || input.getTradeDirection() == null || input.getTradeDate() == null) {
//             throw new MissingFieldException("Shipping Cost, HS Code, Country, Trade Direction, and Trade Date are required fields.");
//         }
//         CalculationResult result = calculationService.calculateTariff(input);
//         return ResponseEntity.ok(result);
//     }
// }
