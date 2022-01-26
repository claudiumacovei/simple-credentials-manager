package com.claudiu.macovei.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.claudiu.macovei.domain.Credential;
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
 * Spring Data Elasticsearch repository for the {@link Credential} entity.
 */
public interface CredentialSearchRepository extends ElasticsearchRepository<Credential, Long>, CredentialSearchRepositoryInternal {}

interface CredentialSearchRepositoryInternal {
    Page<Credential> search(String query, Pageable pageable);
}

class CredentialSearchRepositoryInternalImpl implements CredentialSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    CredentialSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Credential> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Credential> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Credential.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
