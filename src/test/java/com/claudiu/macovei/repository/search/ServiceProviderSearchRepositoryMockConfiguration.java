package com.claudiu.macovei.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ServiceProviderSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ServiceProviderSearchRepositoryMockConfiguration {

    @MockBean
    private ServiceProviderSearchRepository mockServiceProviderSearchRepository;
}
