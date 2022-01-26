package com.claudiu.macovei.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Credential.
 */
@Entity
@Table(name = "credential")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "credential")
public class Credential implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile")
    private String profile;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @OneToOne
    @JoinColumn(unique = true)
    private IdentityProvider identityProvider;

    @OneToMany(mappedBy = "credential")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "credential" }, allowSetters = true)
    private Set<ServiceProvider> serviceProviders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Credential id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfile() {
        return this.profile;
    }

    public Credential profile(String profile) {
        this.setProfile(profile);
        return this;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Credential enabled(Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return this.username;
    }

    public Credential username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public Credential password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public IdentityProvider getIdentityProvider() {
        return this.identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public Credential identityProvider(IdentityProvider identityProvider) {
        this.setIdentityProvider(identityProvider);
        return this;
    }

    public Set<ServiceProvider> getServiceProviders() {
        return this.serviceProviders;
    }

    public void setServiceProviders(Set<ServiceProvider> serviceProviders) {
        if (this.serviceProviders != null) {
            this.serviceProviders.forEach(i -> i.setCredential(null));
        }
        if (serviceProviders != null) {
            serviceProviders.forEach(i -> i.setCredential(this));
        }
        this.serviceProviders = serviceProviders;
    }

    public Credential serviceProviders(Set<ServiceProvider> serviceProviders) {
        this.setServiceProviders(serviceProviders);
        return this;
    }

    public Credential addServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProviders.add(serviceProvider);
        serviceProvider.setCredential(this);
        return this;
    }

    public Credential removeServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProviders.remove(serviceProvider);
        serviceProvider.setCredential(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Credential)) {
            return false;
        }
        return id != null && id.equals(((Credential) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Credential{" +
            "id=" + getId() +
            ", profile='" + getProfile() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
