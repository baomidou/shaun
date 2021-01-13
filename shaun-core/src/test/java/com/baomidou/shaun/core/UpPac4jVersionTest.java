package com.baomidou.shaun.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * @author miemie
 * @since 2021-01-13
 */
class UpPac4jVersionTest extends BaseTokenTest {

    private final static String id = "366e5e14155f49b68167d5a80c39210f";
    private final static String jwt = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..U5cHrCKW4qeBtxgb.FGzoQ" +
            "FnQda1Fr3tIfWgeyVgUKhKOtVWgOGg2mPM82qd6rk13WoDPn_rMd00b5MqNFHi8Llf7Fp0DoLzSrhvZa0ITYNr6_GGhtaIE-y-EonbZMz" +
            "oRf1ZCvj-jW8Ky4Lng9Y9c0obEir9A6rof_XKe4ZqOYSFau6gYroWntu4yJNG4jnIJZvGPVQSo0WPCygyVCCTXqQFXvcFHZ4C1GcO7epJ" +
            "FQp8rNIHIGp1Pj5kE73nN4HGJM_oNI62x1dRIFIL2NXHFgaR9cZQA-ldqj1_3dL875zkzFndkW9-14AEm0FdUebbJtndlD_inWkW0mveK" +
            "fHsyIg1Jyg.IIaLYhm-9dvsGeLq0ysTpg";

    @Test
    void test() {
        TokenProfile profile = (TokenProfile) bothSelector.getAuthenticator().validateToken(jwt);
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(id);
    }
}
