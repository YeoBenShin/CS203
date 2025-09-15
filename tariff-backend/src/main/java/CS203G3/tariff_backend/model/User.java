package CS203G3.tariff_backend.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String uuid;
    // private String username;
    private boolean isAdmin;

    // required by JPA
    public User() {}

    public User(String uuid, boolean isAdmin) {
        this.uuid = uuid;
        this.isAdmin = isAdmin;
    }

    public String getUuid() {
        return uuid;
    }

    // public String getUsername() {
    //     return username;
    // }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    // public void setUsername(String username) {
    //     this.username = username;
    // }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
