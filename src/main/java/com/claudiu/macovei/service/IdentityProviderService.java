package com.claudiu.macovei.service;

import com.claudiu.macovei.domain.IdentityProvider;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link IdentityProvider}.
 */
public interface IdentityProviderService {
    /**
     * Save a identityProvider.
     *
     * @param identityProvider the entity to save.
     * @return the persisted entity.
     */
    IdentityProvider save(IdentityProvider identityProvider);

    /**
     * Partially updates a identityProvider.
     *
     * @param identityProvider the entity to update partially.
     * @return the persisted entity.
     */
    Optional<IdentityProvider> partialUpdate(IdentityProvider identityProvider);

    /**
     * Get all the identityProviders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IdentityProvider> findAll(Pageable pageable);

    /**
     * Get the "id" identityProvider.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<IdentityProvider> findOne(Long id);

    /**
     * Delete the "id" identityProvider.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the identityProvider corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IdentityProvider> search(String query, Pageable pageable);
}
