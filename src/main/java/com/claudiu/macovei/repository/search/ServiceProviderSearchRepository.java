package com.claudiu.macovei.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.claudiu.macovei.domain.ServiceProvider;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ServiceProvider} entity.
 */
public interface ServiceProviderSearchRepository
    extends ElasticsearchRepository<ServiceProvider, Long>, ServiceProviderSearchRepositoryInternal {}

interface ServiceProviderSearchRepositoryInternal {
    Page<ServiceProvider> search(String query, Pageable pageable);
}

class ServiceProviderSearchRepositoryInternalImpl implements ServiceProviderSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ServiceProviderSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<ServiceProvider> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<ServiceProvider> hits = elasticsearchTemplate
            .search(nativeSearchQuery, ServiceProvider.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
