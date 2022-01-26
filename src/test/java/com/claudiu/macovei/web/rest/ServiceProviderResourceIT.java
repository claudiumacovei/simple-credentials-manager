package com.claudiu.macovei.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.claudiu.macovei.IntegrationTest;
import com.claudiu.macovei.domain.ServiceProvider;
import com.claudiu.macovei.repository.ServiceProviderRepository;
import com.claudiu.macovei.repository.search.ServiceProviderSearchRepository;
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
 * Integration tests for the {@link ServiceProviderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ServiceProviderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/service-providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/service-providers";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    /**
     * This repository is mocked in the com.claudiu.macovei.repository.search test package.
     *
     * @see com.claudiu.macovei.repository.search.ServiceProviderSearchRepositoryMockConfiguration
     */
    @Autowired
    private ServiceProviderSearchRepository mockServiceProviderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceProviderMockMvc;

    private ServiceProvider serviceProvider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceProvider createEntity(EntityManager em) {
        ServiceProvider serviceProvider = new ServiceProvider().name(DEFAULT_NAME);
        return serviceProvider;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ServiceProvider createUpdatedEntity(EntityManager em) {
        ServiceProvider serviceProvider = new ServiceProvider().name(UPDATED_NAME);
        return serviceProvider;
    }

    @BeforeEach
    public void initTest() {
        serviceProvider = createEntity(em);
    }

    @Test
    @Transactional
    void createServiceProvider() throws Exception {
        int databaseSizeBeforeCreate = serviceProviderRepository.findAll().size();
        // Create the ServiceProvider
        restServiceProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isCreated());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeCreate + 1);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(1)).save(testServiceProvider);
    }

    @Test
    @Transactional
    void createServiceProviderWithExistingId() throws Exception {
        // Create the ServiceProvider with an existing ID
        serviceProvider.setId(1L);

        int databaseSizeBeforeCreate = serviceProviderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeCreate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void getAllServiceProviders() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get all the serviceProviderList
        restServiceProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        // Get the serviceProvider
        restServiceProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, serviceProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(serviceProvider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingServiceProvider() throws Exception {
        // Get the serviceProvider
        restServiceProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();

        // Update the serviceProvider
        ServiceProvider updatedServiceProvider = serviceProviderRepository.findById(serviceProvider.getId()).get();
        // Disconnect from session so that the updates on updatedServiceProvider are not directly saved in db
        em.detach(updatedServiceProvider);
        updatedServiceProvider.name(UPDATED_NAME);

        restServiceProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedServiceProvider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedServiceProvider))
            )
            .andExpect(status().isOk());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository).save(testServiceProvider);
    }

    @Test
    @Transactional
    void putNonExistingServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, serviceProvider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void putWithIdMismatchServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void partialUpdateServiceProviderWithPatch() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();

        // Update the serviceProvider using partial update
        ServiceProvider partialUpdatedServiceProvider = new ServiceProvider();
        partialUpdatedServiceProvider.setId(serviceProvider.getId());

        restServiceProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServiceProvider))
            )
            .andExpect(status().isOk());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateServiceProviderWithPatch() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();

        // Update the serviceProvider using partial update
        ServiceProvider partialUpdatedServiceProvider = new ServiceProvider();
        partialUpdatedServiceProvider.setId(serviceProvider.getId());

        partialUpdatedServiceProvider.name(UPDATED_NAME);

        restServiceProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedServiceProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedServiceProvider))
            )
            .andExpect(status().isOk());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);
        ServiceProvider testServiceProvider = serviceProviderList.get(serviceProviderList.size() - 1);
        assertThat(testServiceProvider.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, serviceProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void patchWithIdMismatchServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isBadRequest());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamServiceProvider() throws Exception {
        int databaseSizeBeforeUpdate = serviceProviderRepository.findAll().size();
        serviceProvider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceProviderMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(serviceProvider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ServiceProvider in the database
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(0)).save(serviceProvider);
    }

    @Test
    @Transactional
    void deleteServiceProvider() throws Exception {
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);

        int databaseSizeBeforeDelete = serviceProviderRepository.findAll().size();

        // Delete the serviceProvider
        restServiceProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, serviceProvider.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findAll();
        assertThat(serviceProviderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ServiceProvider in Elasticsearch
        verify(mockServiceProviderSearchRepository, times(1)).deleteById(serviceProvider.getId());
    }

    @Test
    @Transactional
    void searchServiceProvider() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        serviceProviderRepository.saveAndFlush(serviceProvider);
        when(mockServiceProviderSearchRepository.search("id:" + serviceProvider.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(serviceProvider), PageRequest.of(0, 1), 1));

        // Search the serviceProvider
        restServiceProviderMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + serviceProvider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceProvider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
