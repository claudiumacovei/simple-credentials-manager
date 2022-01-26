package com.claudiu.macovei.repository;

import com.claudiu.macovei.domain.IdentityProvider;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the IdentityProvider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IdentityProviderRepository extends JpaRepository<IdentityProvider, Long> {}
