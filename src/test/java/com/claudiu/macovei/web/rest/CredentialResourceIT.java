package com.claudiu.macovei.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.claudiu.macovei.IntegrationTest;
import com.claudiu.macovei.domain.Credential;
import com.claudiu.macovei.repository.CredentialRepository;
import com.claudiu.macovei.repository.search.CredentialSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CredentialResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CredentialResourceIT {

    private static final String DEFAULT_PROFILE = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/credentials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/credentials";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CredentialRepository credentialRepository;

    /**
     * This repository is mocked in the com.claudiu.macovei.repository.search test package.
     *
     * @see com.claudiu.macovei.repository.search.CredentialSearchRepositoryMockConfiguration
     */
    @Autowired
    private CredentialSearchRepository mockCredentialSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCredentialMockMvc;

    private Credential credential;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Credential createEntity(EntityManager em) {
        Credential credential = new Credential()
            .profile(DEFAULT_PROFILE)
            .enabled(DEFAULT_ENABLED)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD);
        return credential;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Credential createUpdatedEntity(EntityManager em) {
        Credential credential = new Credential()
            .profile(UPDATED_PROFILE)
            .enabled(UPDATED_ENABLED)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD);
        return credential;
    }

    @BeforeEach
    public void initTest() {
        credential = createEntity(em);
    }

    @Test
    @Transactional
    void createCredential() throws Exception {
        int databaseSizeBeforeCreate = credentialRepository.findAll().size();
        // Create the Credential
        restCredentialMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isCreated());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeCreate + 1);
        Credential testCredential = credentialList.get(credentialList.size() - 1);
        assertThat(testCredential.getProfile()).isEqualTo(DEFAULT_PROFILE);
        assertThat(testCredential.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testCredential.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testCredential.getPassword()).isEqualTo(DEFAULT_PASSWORD);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(1)).save(testCredential);
    }

    @Test
    @Transactional
    void createCredentialWithExistingId() throws Exception {
        // Create the Credential with an existing ID
        credential.setId(1L);

        int databaseSizeBeforeCreate = credentialRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCredentialMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isBadRequest());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeCreate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void getAllCredentials() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        // Get all the credentialList
        restCredentialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(credential.getId().intValue())))
            .andExpect(jsonPath("$.[*].profile").value(hasItem(DEFAULT_PROFILE)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }

    @Test
    @Transactional
    void getCredential() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        // Get the credential
        restCredentialMockMvc
            .perform(get(ENTITY_API_URL_ID, credential.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(credential.getId().intValue()))
            .andExpect(jsonPath("$.profile").value(DEFAULT_PROFILE))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }

    @Test
    @Transactional
    void getNonExistingCredential() throws Exception {
        // Get the credential
        restCredentialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCredential() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();

        // Update the credential
        Credential updatedCredential = credentialRepository.findById(credential.getId()).get();
        // Disconnect from session so that the updates on updatedCredential are not directly saved in db
        em.detach(updatedCredential);
        updatedCredential.profile(UPDATED_PROFILE).enabled(UPDATED_ENABLED).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restCredentialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCredential.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCredential))
            )
            .andExpect(status().isOk());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);
        Credential testCredential = credentialList.get(credentialList.size() - 1);
        assertThat(testCredential.getProfile()).isEqualTo(UPDATED_PROFILE);
        assertThat(testCredential.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testCredential.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCredential.getPassword()).isEqualTo(UPDATED_PASSWORD);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository).save(testCredential);
    }

    @Test
    @Transactional
    void putNonExistingCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, credential.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isBadRequest());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void putWithIdMismatchCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isBadRequest());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void partialUpdateCredentialWithPatch() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();

        // Update the credential using partial update
        Credential partialUpdatedCredential = new Credential();
        partialUpdatedCredential.setId(credential.getId());

        partialUpdatedCredential.profile(UPDATED_PROFILE).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restCredentialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCredential.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCredential))
            )
            .andExpect(status().isOk());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);
        Credential testCredential = credentialList.get(credentialList.size() - 1);
        assertThat(testCredential.getProfile()).isEqualTo(UPDATED_PROFILE);
        assertThat(testCredential.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testCredential.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCredential.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void fullUpdateCredentialWithPatch() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();

        // Update the credential using partial update
        Credential partialUpdatedCredential = new Credential();
        partialUpdatedCredential.setId(credential.getId());

        partialUpdatedCredential.profile(UPDATED_PROFILE).enabled(UPDATED_ENABLED).username(UPDATED_USERNAME).password(UPDATED_PASSWORD);

        restCredentialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCredential.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCredential))
            )
            .andExpect(status().isOk());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);
        Credential testCredential = credentialList.get(credentialList.size() - 1);
        assertThat(testCredential.getProfile()).isEqualTo(UPDATED_PROFILE);
        assertThat(testCredential.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testCredential.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testCredential.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void patchNonExistingCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, credential.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isBadRequest());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isBadRequest());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCredential() throws Exception {
        int databaseSizeBeforeUpdate = credentialRepository.findAll().size();
        credential.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCredentialMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(credential))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Credential in the database
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(0)).save(credential);
    }

    @Test
    @Transactional
    void deleteCredential() throws Exception {
        // Initialize the database
        credentialRepository.saveAndFlush(credential);

        int databaseSizeBeforeDelete = credentialRepository.findAll().size();

        // Delete the credential
        restCredentialMockMvc
            .perform(delete(ENTITY_API_URL_ID, credential.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Credential> credentialList = credentialRepository.findAll();
        assertThat(credentialList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Credential in Elasticsearch
        verify(mockCredentialSearchRepository, times(1)).deleteById(credential.getId());
    }

    @Test
    @Transactional
    void searchCredential() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        credentialRepository.saveAndFlush(credential);
        when(mockCredentialSearchRepository.search("id:" + credential.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(credential), PageRequest.of(0, 1), 1));

        // Search the credential
        restCredentialMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + credential.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(credential.getId().intValue())))
            .andExpect(jsonPath("$.[*].profile").value(hasItem(DEFAULT_PROFILE)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }
}
