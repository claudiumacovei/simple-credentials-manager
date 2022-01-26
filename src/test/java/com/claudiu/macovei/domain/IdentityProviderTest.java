package com.claudiu.macovei.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.claudiu.macovei.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IdentityProviderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IdentityProvider.class);
        IdentityProvider identityProvider1 = new IdentityProvider();
        identityProvider1.setId(1L);
        IdentityProvider identityProvider2 = new IdentityProvider();
        identityProvider2.setId(identityProvider1.getId());
        assertThat(identityProvider1).isEqualTo(identityProvider2);
        identityProvider2.setId(2L);
        assertThat(identityProvider1).isNotEqualTo(identityProvider2);
        identityProvider1.setId(null);
        assertThat(identityProvider1).isNotEqualTo(identityProvider2);
    }
}
