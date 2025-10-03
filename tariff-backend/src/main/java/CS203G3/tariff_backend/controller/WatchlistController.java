package CS203G3.tariff_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CS203G3.tariff_backend.dto.WatchlistCreationDto;
import CS203G3.tariff_backend.dto.WatchlistRequestDto;
import CS203G3.tariff_backend.model.Watchlist;
import CS203G3.tariff_backend.repository.WatchlistRepository;
import CS203G3.tariff_backend.service.WatchlistService;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
public class WatchlistController {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchlistService watchlistService;

    /**
     * Get all watchlists
     * GET /api/watchlists
     */
    @GetMapping
    public ResponseEntity<List<WatchlistRequestDto>> getAllWatchlists() {
        List<WatchlistRequestDto> watchlists = watchlistService.getAllWatchlists();
        return ResponseEntity.ok(watchlists);
    }

    /**
     * Create a new watchlist
     * POST /api/watchlists
     */
    @PostMapping
    public ResponseEntity<List<Watchlist>> createWatchlist(@RequestBody WatchlistCreationDto watchlist) {
        List<Watchlist> watchlistsToCreate = watchlistService.createWatchlist(watchlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(watchlistsToCreate);
    }

    /**
     * Delete a watchlist by ID
     * DELETE /api/watchlists/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable String id) {
        watchlistRepository.deleteById(Long.parseLong(id));
        return ResponseEntity.noContent().build();
    }
}
