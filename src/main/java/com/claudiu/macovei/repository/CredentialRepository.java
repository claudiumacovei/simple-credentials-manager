package com.claudiu.macovei.repository;

import com.claudiu.macovei.domain.Credential;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Credential entity.
 */
@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    @Query(
        value = "select distinct credential from Credential credential left join fetch credential.serviceProviders",
        countQuery = "select count(distinct credential) from Credential credential"
    )
    Page<Credential> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct credential from Credential credential left join fetch credential.serviceProviders")
    List<Credential> findAllWithEagerRelationships();

    @Query("select credential from Credential credential left join fetch credential.serviceProviders where credential.id =:id")
    Optional<Credential> findOneWithEagerRelationships(@Param("id") Long id);
}
