package com.claudiu.macovei.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link IdentityProviderSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class IdentityProviderSearchRepositoryMockConfiguration {

    @MockBean
    private IdentityProviderSearchRepository mockIdentityProviderSearchRepository;
}
