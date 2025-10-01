package CS203G3.tariff_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "watchlist")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watchlist_id")
    private Long watchlistID;

    @ManyToOne
    @JoinColumn(name = "uuid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    public Watchlist() {}

    public Watchlist(User user, Tariff tariff) {
        this.user = user;
        this.tariff = tariff;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public Long getWatchlistID() {
        return watchlistID;
    }

}
