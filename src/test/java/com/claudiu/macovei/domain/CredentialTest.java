package com.claudiu.macovei.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.claudiu.macovei.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CredentialTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Credential.class);
        Credential credential1 = new Credential();
        credential1.setId(1L);
        Credential credential2 = new Credential();
        credential2.setId(credential1.getId());
        assertThat(credential1).isEqualTo(credential2);
        credential2.setId(2L);
        assertThat(credential1).isNotEqualTo(credential2);
        credential1.setId(null);
        assertThat(credential1).isNotEqualTo(credential2);
    }
}
