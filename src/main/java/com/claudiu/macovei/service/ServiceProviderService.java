package com.claudiu.macovei.service;

import com.claudiu.macovei.domain.ServiceProvider;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ServiceProvider}.
 */
public interface ServiceProviderService {
    /**
     * Save a serviceProvider.
     *
     * @param serviceProvider the entity to save.
     * @return the persisted entity.
     */
    ServiceProvider save(ServiceProvider serviceProvider);

    /**
     * Partially updates a serviceProvider.
     *
     * @param serviceProvider the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ServiceProvider> partialUpdate(ServiceProvider serviceProvider);

    /**
     * Get all the serviceProviders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceProvider> findAll(Pageable pageable);

    /**
     * Get the "id" serviceProvider.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ServiceProvider> findOne(Long id);

    /**
     * Delete the "id" serviceProvider.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the serviceProvider corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ServiceProvider> search(String query, Pageable pageable);
}
