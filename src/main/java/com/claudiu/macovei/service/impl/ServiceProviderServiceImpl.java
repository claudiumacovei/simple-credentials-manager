package com.claudiu.macovei.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.claudiu.macovei.domain.ServiceProvider;
import com.claudiu.macovei.repository.ServiceProviderRepository;
import com.claudiu.macovei.repository.search.ServiceProviderSearchRepository;
import com.claudiu.macovei.service.ServiceProviderService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ServiceProvider}.
 */
@Service
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final Logger log = LoggerFactory.getLogger(ServiceProviderServiceImpl.class);

    private final ServiceProviderRepository serviceProviderRepository;

    private final ServiceProviderSearchRepository serviceProviderSearchRepository;

    public ServiceProviderServiceImpl(
        ServiceProviderRepository serviceProviderRepository,
        ServiceProviderSearchRepository serviceProviderSearchRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceProviderSearchRepository = serviceProviderSearchRepository;
    }

    @Override
    public ServiceProvider save(ServiceProvider serviceProvider) {
        log.debug("Request to save ServiceProvider : {}", serviceProvider);
        ServiceProvider result = serviceProviderRepository.save(serviceProvider);
        serviceProviderSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<ServiceProvider> partialUpdate(ServiceProvider serviceProvider) {
        log.debug("Request to partially update ServiceProvider : {}", serviceProvider);

        return serviceProviderRepository
            .findById(serviceProvider.getId())
            .map(existingServiceProvider -> {
                if (serviceProvider.getName() != null) {
                    existingServiceProvider.setName(serviceProvider.getName());
                }

                return existingServiceProvider;
            })
            .map(serviceProviderRepository::save)
            .map(savedServiceProvider -> {
                serviceProviderSearchRepository.save(savedServiceProvider);

                return savedServiceProvider;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceProvider> findAll(Pageable pageable) {
        log.debug("Request to get all ServiceProviders");
        return serviceProviderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceProvider> findOne(Long id) {
        log.debug("Request to get ServiceProvider : {}", id);
        return serviceProviderRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ServiceProvider : {}", id);
        serviceProviderRepository.deleteById(id);
        serviceProviderSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceProvider> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ServiceProviders for query {}", query);
        return serviceProviderSearchRepository.search(query, pageable);
    }
}
