package com.claudiu.macovei.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.claudiu.macovei.domain.Credential;
import com.claudiu.macovei.repository.CredentialRepository;
import com.claudiu.macovei.repository.search.CredentialSearchRepository;
import com.claudiu.macovei.service.CredentialService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Credential}.
 */
@Service
@Transactional
public class CredentialServiceImpl implements CredentialService {

    private final Logger log = LoggerFactory.getLogger(CredentialServiceImpl.class);

    private final CredentialRepository credentialRepository;

    private final CredentialSearchRepository credentialSearchRepository;

    public CredentialServiceImpl(CredentialRepository credentialRepository, CredentialSearchRepository credentialSearchRepository) {
        this.credentialRepository = credentialRepository;
        this.credentialSearchRepository = credentialSearchRepository;
    }

    @Override
    public Credential save(Credential credential) {
        log.debug("Request to save Credential : {}", credential);
        Credential result = credentialRepository.save(credential);
        credentialSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Credential> partialUpdate(Credential credential) {
        log.debug("Request to partially update Credential : {}", credential);

        return credentialRepository
            .findById(credential.getId())
            .map(existingCredential -> {
                if (credential.getProfile() != null) {
                    existingCredential.setProfile(credential.getProfile());
                }
                if (credential.getEnabled() != null) {
                    existingCredential.setEnabled(credential.getEnabled());
                }
                if (credential.getUsername() != null) {
                    existingCredential.setUsername(credential.getUsername());
                }
                if (credential.getPassword() != null) {
                    existingCredential.setPassword(credential.getPassword());
                }

                return existingCredential;
            })
            .map(credentialRepository::save)
            .map(savedCredential -> {
                credentialSearchRepository.save(savedCredential);

                return savedCredential;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Credential> findAll(Pageable pageable) {
        log.debug("Request to get all Credentials");
        return credentialRepository.findAll(pageable);
    }

    public Page<Credential> findAllWithEagerRelationships(Pageable pageable) {
        return credentialRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Credential> findOne(Long id) {
        log.debug("Request to get Credential : {}", id);
        return credentialRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Credential : {}", id);
        credentialRepository.deleteById(id);
        credentialSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Credential> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Credentials for query {}", query);
        return credentialSearchRepository.search(query, pageable);
    }
}
