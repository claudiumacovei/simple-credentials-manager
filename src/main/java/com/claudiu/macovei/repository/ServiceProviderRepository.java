package com.claudiu.macovei.repository;

import com.claudiu.macovei.domain.ServiceProvider;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ServiceProvider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {}
