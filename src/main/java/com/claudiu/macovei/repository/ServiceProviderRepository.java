package com.claudiu.macovei.repository;

import com.claudiu.macovei.domain.ServiceProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ServiceProvider entity.
 */
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    @Query(
        value = "select distinct serviceProvider from ServiceProvider serviceProvider left join fetch serviceProvider.credentials",
        countQuery = "select count(distinct serviceProvider) from ServiceProvider serviceProvider"
    )
    Page<ServiceProvider> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct serviceProvider from ServiceProvider serviceProvider left join fetch serviceProvider.credentials")
    List<ServiceProvider> findAllWithEagerRelationships();

    @Query(
        "select serviceProvider from ServiceProvider serviceProvider left join fetch serviceProvider.credentials where serviceProvider.id =:id"
    )
    Optional<ServiceProvider> findOneWithEagerRelationships(@Param("id") Long id);
}
