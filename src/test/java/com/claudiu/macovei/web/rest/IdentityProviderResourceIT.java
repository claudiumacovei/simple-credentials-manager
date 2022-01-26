package com.claudiu.macovei.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.claudiu.macovei.IntegrationTest;
import com.claudiu.macovei.domain.IdentityProvider;
import com.claudiu.macovei.repository.IdentityProviderRepository;
import com.claudiu.macovei.repository.search.IdentityProviderSearchRepository;
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
 * Integration tests for the {@link IdentityProviderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IdentityProviderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/identity-providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/identity-providers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IdentityProviderRepository identityProviderRepository;

    /**
     * This repository is mocked in the com.claudiu.macovei.repository.search test package.
     *
     * @see com.claudiu.macovei.repository.search.IdentityProviderSearchRepositoryMockConfiguration
     */
    @Autowired
    private IdentityProviderSearchRepository mockIdentityProviderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIdentityProviderMockMvc;

    private IdentityProvider identityProvider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IdentityProvider createEntity(EntityManager em) {
        IdentityProvider identityProvider = new IdentityProvider().name(DEFAULT_NAME);
        return identityProvider;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IdentityProvider createUpdatedEntity(EntityManager em) {
        IdentityProvider identityProvider = new IdentityProvider().name(UPDATED_NAME);
        return identityProvider;
    }

    @BeforeEach
    public void initTest() {
        identityProvider = createEntity(em);
    }

    @Test
    @Transactional
    void createIdentityProvider() throws Exception {
        int databaseSizeBeforeCreate = identityProviderRepository.findAll().size();
        // Create the IdentityProvider
        restIdentityProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isCreated());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeCreate + 1);
        IdentityProvider testIdentityProvider = identityProviderList.get(identityProviderList.size() - 1);
        assertThat(testIdentityProvider.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(1)).save(testIdentityProvider);
    }

    @Test
    @Transactional
    void createIdentityProviderWithExistingId() throws Exception {
        // Create the IdentityProvider with an existing ID
        identityProvider.setId(1L);

        int databaseSizeBeforeCreate = identityProviderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIdentityProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeCreate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void getAllIdentityProviders() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        // Get all the identityProviderList
        restIdentityProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(identityProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getIdentityProvider() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        // Get the identityProvider
        restIdentityProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, identityProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(identityProvider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingIdentityProvider() throws Exception {
        // Get the identityProvider
        restIdentityProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIdentityProvider() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();

        // Update the identityProvider
        IdentityProvider updatedIdentityProvider = identityProviderRepository.findById(identityProvider.getId()).get();
        // Disconnect from session so that the updates on updatedIdentityProvider are not directly saved in db
        em.detach(updatedIdentityProvider);
        updatedIdentityProvider.name(UPDATED_NAME);

        restIdentityProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIdentityProvider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIdentityProvider))
            )
            .andExpect(status().isOk());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);
        IdentityProvider testIdentityProvider = identityProviderList.get(identityProviderList.size() - 1);
        assertThat(testIdentityProvider.getName()).isEqualTo(UPDATED_NAME);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository).save(testIdentityProvider);
    }

    @Test
    @Transactional
    void putNonExistingIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, identityProvider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void putWithIdMismatchIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void partialUpdateIdentityProviderWithPatch() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();

        // Update the identityProvider using partial update
        IdentityProvider partialUpdatedIdentityProvider = new IdentityProvider();
        partialUpdatedIdentityProvider.setId(identityProvider.getId());

        partialUpdatedIdentityProvider.name(UPDATED_NAME);

        restIdentityProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIdentityProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIdentityProvider))
            )
            .andExpect(status().isOk());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);
        IdentityProvider testIdentityProvider = identityProviderList.get(identityProviderList.size() - 1);
        assertThat(testIdentityProvider.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateIdentityProviderWithPatch() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();

        // Update the identityProvider using partial update
        IdentityProvider partialUpdatedIdentityProvider = new IdentityProvider();
        partialUpdatedIdentityProvider.setId(identityProvider.getId());

        partialUpdatedIdentityProvider.name(UPDATED_NAME);

        restIdentityProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIdentityProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIdentityProvider))
            )
            .andExpect(status().isOk());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);
        IdentityProvider testIdentityProvider = identityProviderList.get(identityProviderList.size() - 1);
        assertThat(testIdentityProvider.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, identityProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIdentityProvider() throws Exception {
        int databaseSizeBeforeUpdate = identityProviderRepository.findAll().size();
        identityProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIdentityProviderMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(identityProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IdentityProvider in the database
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(0)).save(identityProvider);
    }

    @Test
    @Transactional
    void deleteIdentityProvider() throws Exception {
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);

        int databaseSizeBeforeDelete = identityProviderRepository.findAll().size();

        // Delete the identityProvider
        restIdentityProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, identityProvider.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IdentityProvider> identityProviderList = identityProviderRepository.findAll();
        assertThat(identityProviderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the IdentityProvider in Elasticsearch
        verify(mockIdentityProviderSearchRepository, times(1)).deleteById(identityProvider.getId());
    }

    @Test
    @Transactional
    void searchIdentityProvider() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        identityProviderRepository.saveAndFlush(identityProvider);
        when(mockIdentityProviderSearchRepository.search("id:" + identityProvider.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(identityProvider), PageRequest.of(0, 1), 1));

        // Search the identityProvider
        restIdentityProviderMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + identityProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(identityProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
