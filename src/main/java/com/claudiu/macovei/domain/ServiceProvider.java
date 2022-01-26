package com.claudiu.macovei.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ServiceProvider.
 */
@Entity
@Table(name = "service_provider")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "serviceprovider")
public class ServiceProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "serviceProviders")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "identityProvider", "serviceProviders" }, allowSetters = true)
    private Set<Credential> credentials = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ServiceProvider id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ServiceProvider name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Credential> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(Set<Credential> credentials) {
        if (this.credentials != null) {
            this.credentials.forEach(i -> i.removeServiceProvider(this));
        }
        if (credentials != null) {
            credentials.forEach(i -> i.addServiceProvider(this));
        }
        this.credentials = credentials;
    }

    public ServiceProvider credentials(Set<Credential> credentials) {
        this.setCredentials(credentials);
        return this;
    }

    public ServiceProvider addCredential(Credential credential) {
        this.credentials.add(credential);
        credential.getServiceProviders().add(this);
        return this;
    }

    public ServiceProvider removeCredential(Credential credential) {
        this.credentials.remove(credential);
        credential.getServiceProviders().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceProvider)) {
            return false;
        }
        return id != null && id.equals(((ServiceProvider) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceProvider{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
