package com.claudiu.macovei.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.claudiu.macovei.domain.ServiceProvider;
import com.claudiu.macovei.repository.ServiceProviderRepository;
import com.claudiu.macovei.service.ServiceProviderService;
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
 * REST controller for managing {@link com.claudiu.macovei.domain.ServiceProvider}.
 */
@RestController
@RequestMapping("/api")
public class ServiceProviderResource {

    private final Logger log = LoggerFactory.getLogger(ServiceProviderResource.class);

    private static final String ENTITY_NAME = "serviceProvider";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ServiceProviderService serviceProviderService;

    private final ServiceProviderRepository serviceProviderRepository;

    public ServiceProviderResource(ServiceProviderService serviceProviderService, ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderService = serviceProviderService;
        this.serviceProviderRepository = serviceProviderRepository;
    }

    /**
     * {@code POST  /service-providers} : Create a new serviceProvider.
     *
     * @param serviceProvider the serviceProvider to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new serviceProvider, or with status {@code 400 (Bad Request)} if the serviceProvider has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/service-providers")
    public ResponseEntity<ServiceProvider> createServiceProvider(@RequestBody ServiceProvider serviceProvider) throws URISyntaxException {
        log.debug("REST request to save ServiceProvider : {}", serviceProvider);
        if (serviceProvider.getId() != null) {
            throw new BadRequestAlertException("A new serviceProvider cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ServiceProvider result = serviceProviderService.save(serviceProvider);
        return ResponseEntity
            .created(new URI("/api/service-providers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /service-providers/:id} : Updates an existing serviceProvider.
     *
     * @param id the id of the serviceProvider to save.
     * @param serviceProvider the serviceProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceProvider,
     * or with status {@code 400 (Bad Request)} if the serviceProvider is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/service-providers/{id}")
    public ResponseEntity<ServiceProvider> updateServiceProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ServiceProvider serviceProvider
    ) throws URISyntaxException {
        log.debug("REST request to update ServiceProvider : {}, {}", id, serviceProvider);
        if (serviceProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ServiceProvider result = serviceProviderService.save(serviceProvider);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceProvider.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /service-providers/:id} : Partial updates given fields of an existing serviceProvider, field will ignore if it is null
     *
     * @param id the id of the serviceProvider to save.
     * @param serviceProvider the serviceProvider to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated serviceProvider,
     * or with status {@code 400 (Bad Request)} if the serviceProvider is not valid,
     * or with status {@code 404 (Not Found)} if the serviceProvider is not found,
     * or with status {@code 500 (Internal Server Error)} if the serviceProvider couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/service-providers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ServiceProvider> partialUpdateServiceProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ServiceProvider serviceProvider
    ) throws URISyntaxException {
        log.debug("REST request to partial update ServiceProvider partially : {}, {}", id, serviceProvider);
        if (serviceProvider.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, serviceProvider.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!serviceProviderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ServiceProvider> result = serviceProviderService.partialUpdate(serviceProvider);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceProvider.getId().toString())
        );
    }

    /**
     * {@code GET  /service-providers} : get all the serviceProviders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceProviders in body.
     */
    @GetMapping("/service-providers")
    public ResponseEntity<List<ServiceProvider>> getAllServiceProviders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ServiceProviders");
        Page<ServiceProvider> page = serviceProviderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /service-providers/:id} : get the "id" serviceProvider.
     *
     * @param id the id of the serviceProvider to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the serviceProvider, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/service-providers/{id}")
    public ResponseEntity<ServiceProvider> getServiceProvider(@PathVariable Long id) {
        log.debug("REST request to get ServiceProvider : {}", id);
        Optional<ServiceProvider> serviceProvider = serviceProviderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceProvider);
    }

    /**
     * {@code DELETE  /service-providers/:id} : delete the "id" serviceProvider.
     *
     * @param id the id of the serviceProvider to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/service-providers/{id}")
    public ResponseEntity<Void> deleteServiceProvider(@PathVariable Long id) {
        log.debug("REST request to delete ServiceProvider : {}", id);
        serviceProviderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/service-providers?query=:query} : search for the serviceProvider corresponding
     * to the query.
     *
     * @param query the query of the serviceProvider search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/service-providers")
    public ResponseEntity<List<ServiceProvider>> searchServiceProviders(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of ServiceProviders for query {}", query);
        Page<ServiceProvider> page = serviceProviderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
