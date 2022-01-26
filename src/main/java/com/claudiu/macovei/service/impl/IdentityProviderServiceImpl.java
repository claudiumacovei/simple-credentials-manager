package com.claudiu.macovei.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.claudiu.macovei.domain.IdentityProvider;
import com.claudiu.macovei.repository.IdentityProviderRepository;
import com.claudiu.macovei.repository.search.IdentityProviderSearchRepository;
import com.claudiu.macovei.service.IdentityProviderService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link IdentityProvider}.
 */
@Service
@Transactional
public class IdentityProviderServiceImpl implements IdentityProviderService {

    private final Logger log = LoggerFactory.getLogger(IdentityProviderServiceImpl.class);

    private final IdentityProviderRepository identityProviderRepository;

    private final IdentityProviderSearchRepository identityProviderSearchRepository;

    public IdentityProviderServiceImpl(
        IdentityProviderRepository identityProviderRepository,
        IdentityProviderSearchRepository identityProviderSearchRepository
    ) {
        this.identityProviderRepository = identityProviderRepository;
        this.identityProviderSearchRepository = identityProviderSearchRepository;
    }

    @Override
    public IdentityProvider save(IdentityProvider identityProvider) {
        log.debug("Request to save IdentityProvider : {}", identityProvider);
        IdentityProvider result = identityProviderRepository.save(identityProvider);
        identityProviderSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<IdentityProvider> partialUpdate(IdentityProvider identityProvider) {
        log.debug("Request to partially update IdentityProvider : {}", identityProvider);

        return identityProviderRepository
            .findById(identityProvider.getId())
            .map(existingIdentityProvider -> {
                if (identityProvider.getName() != null) {
                    existingIdentityProvider.setName(identityProvider.getName());
                }

                return existingIdentityProvider;
            })
            .map(identityProviderRepository::save)
            .map(savedIdentityProvider -> {
                identityProviderSearchRepository.save(savedIdentityProvider);

                return savedIdentityProvider;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IdentityProvider> findAll(Pageable pageable) {
        log.debug("Request to get all IdentityProviders");
        return identityProviderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdentityProvider> findOne(Long id) {
        log.debug("Request to get IdentityProvider : {}", id);
        return identityProviderRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete IdentityProvider : {}", id);
        identityProviderRepository.deleteById(id);
        identityProviderSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IdentityProvider> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of IdentityProviders for query {}", query);
        return identityProviderSearchRepository.search(query, pageable);
    }
}
