package CS203G3.tariff_backend.service;

import CS203G3.tariff_backend.model.Tariff;
import CS203G3.tariff_backend.exception.TariffNotFoundException;

import java.util.List;


/**
 * Service interface for managing tariff operations.
 *
 * This interface defines the core business logic operations for tariff management,
 * including CRUD (Create, Read, Update, Delete) operations. Implementations of
 * this interface should handle the business rules.
 *
 */
public interface TariffService {

    /**
     * Retrieves all tariffs in the system.
     *
     * @return a list of all tariffs currently stored in the system.
     *         Returns an empty list if no tariffs are found.
     */
    List<Tariff> listTariffs();

    /**
     * Retrieves a specific tariff by its unique identifier.
     *
     * @param id the unique identifier of the tariff to retrieve.
     * @return the tariff with the specified ID
     * @throws TariffNotFoundException if no tariff exists with the given ID
     */
    Tariff getTariff(Long id);

    /**
     * Adds a new tariff to the system.
     *
     * The tariff will be assigned a unique ID automatically. If the tariff
     * already has an ID, it will be ignored and a new ID will be generated.
     *
     * @param tariff the tariff to add to the system.
     * @return the newly created tariff with its assigned ID
     */
    Tariff addTariff(Tariff tariff);

    /**
     * Updates an existing tariff's information.
     *
     * Only the tariff's title and other mutable properties will be updated.
     * The tariff's ID cannot be changed through this operation.
     *
     * @param id the unique identifier of the Tariff to update.
     * @param tariff the Tariff object containing the updated information.
     * @return the updated tariff
     * @throws TariffNotFoundException if no tariff exists with the given ID
     */
    Tariff updateTariff(Long id, Tariff tariff);

    /**
     * Removes a tariff from the system.
     *
     * @param id the unique identifier of the tariff to delete.
     * @return the deleted tariff object for confirmation
     * @throws TariffNotFoundException if no tariff exists with the given ID
     */
    Tariff deleteTariff(Long id);
}