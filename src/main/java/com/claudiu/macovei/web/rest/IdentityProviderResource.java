package com.claudiu.macovei.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.claudiu.macovei.domain.IdentityProvider;
import com.claudiu.macovei.repository.IdentityProviderRepository;
import com.claudiu.macovei.service.IdentityProviderService;
import com.claudiu.macovei.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.claudiu.macovei.domain.IdentityProvider}.
 */
@RestController
@RequestMapping("/api")
public class IdentityProviderResource {

    private final Logger log = LoggerFactory.getLogger(IdentityProviderResource.class);

    private static final String ENTITY_NAME = "identityProvider";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IdentityProviderService identityProviderService;

    private final IdentityProviderRepository identityProviderRepository;

    public IdentityProviderResource(
        IdentityProviderService identityProviderService,
        IdentityProviderRepository identityProviderRepository
    ) {
        this.identityProviderService = identityProviderService;
        this.identityProviderRepository = identityProviderRepository;
    }

    /**
     * {@code POST  /identity-providers} : Create a new identityProvider.
     *
     * @param identityProvider the identityProvider to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new identityProvider, or with status {@code 400 (Bad Request)} if the identityProvider has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/identity-providers")
    public ResponseEntity<IdentityProvider> createIdentityProvider(@RequestBody IdentityProvider identityProvider)
        throws URISyntaxException {
        log.debug("REST request to save IdentityProvider : {}", identityProvider);
        if (identityProvider.getId() != null) {
            throw new BadRequestAlertException("A new identityProvider cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IdentityProvider result = identityProviderService.save(identityProvider);
        return ResponseEntity
            .created(new URI("/api/identity-providers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /identity-providers/:id} : Updates an existing identityProvider.
     *
     * @param id the id of the identityProvider to save.
     * @param identityProvider the identityProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated identityProvider,
     * or with status {@code 400 (Bad Request)} if the identityProvider is not valid,
     * or with status {@code 500 (Internal Server Error)} if the identityProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/identity-providers/{id}")
    public ResponseEntity<IdentityProvider> updateIdentityProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IdentityProvider identityProvider
    ) throws URISyntaxException {
        log.debug("REST request to update IdentityProvider : {}, {}", id, identityProvider);
        if (identityProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, identityProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!identityProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IdentityProvider result = identityProviderService.save(identityProvider);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, identityProvider.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /identity-providers/:id} : Partial updates given fields of an existing identityProvider, field will ignore if it is null
     *
     * @param id the id of the identityProvider to save.
     * @param identityProvider the identityProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated identityProvider,
     * or with status {@code 400 (Bad Request)} if the identityProvider is not valid,
     * or with status {@code 404 (Not Found)} if the identityProvider is not found,
     * or with status {@code 500 (Internal Server Error)} if the identityProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/identity-providers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IdentityProvider> partialUpdateIdentityProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody IdentityProvider identityProvider
    ) throws URISyntaxException {
        log.debug("REST request to partial update IdentityProvider partially : {}, {}", id, identityProvider);
        if (identityProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, identityProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!identityProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IdentityProvider> result = identityProviderService.partialUpdate(identityProvider);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, identityProvider.getId().toString())
        );
    }

    /**
     * {@code GET  /identity-providers} : get all the identityProviders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of identityProviders in body.
     */
    @GetMapping("/identity-providers")
    public ResponseEntity<List<IdentityProvider>> getAllIdentityProviders(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of IdentityProviders");
        Page<IdentityProvider> page = identityProviderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /identity-providers/:id} : get the "id" identityProvider.
     *
     * @param id the id of the identityProvider to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the identityProvider, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/identity-providers/{id}")
    public ResponseEntity<IdentityProvider> getIdentityProvider(@PathVariable Long id) {
        log.debug("REST request to get IdentityProvider : {}", id);
        Optional<IdentityProvider> identityProvider = identityProviderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(identityProvider);
    }

    /**
     * {@code DELETE  /identity-providers/:id} : delete the "id" identityProvider.
     *
     * @param id the id of the identityProvider to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/identity-providers/{id}")
    public ResponseEntity<Void> deleteIdentityProvider(@PathVariable Long id) {
        log.debug("REST request to delete IdentityProvider : {}", id);
        identityProviderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/identity-providers?query=:query} : search for the identityProvider corresponding
     * to the query.
     *
     * @param query the query of the identityProvider search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/identity-providers")
    public ResponseEntity<List<IdentityProvider>> searchIdentityProviders(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of IdentityProviders for query {}", query);
        Page<IdentityProvider> page = identityProviderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
