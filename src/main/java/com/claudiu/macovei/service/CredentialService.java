package com.claudiu.macovei.service;

import com.claudiu.macovei.domain.Credential;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Credential}.
 */
public interface CredentialService {
    /**
     * Save a credential.
     *
     * @param credential the entity to save.
     * @return the persisted entity.
     */
    Credential save(Credential credential);

    /**
     * Partially updates a credential.
     *
     * @param credential the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Credential> partialUpdate(Credential credential);

    /**
     * Get all the credentials.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Credential> findAll(Pageable pageable);

    /**
     * Get all the credentials with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Credential> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" credential.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Credential> findOne(Long id);

    /**
     * Delete the "id" credential.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the credential corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Credential> search(String query, Pageable pageable);
}
