package com.sree.swingengine.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;

public class TotpGenerator {

    private static final GoogleAuthenticator GOOGLE_AUTHENTICATOR =
            new GoogleAuthenticator();

    public static int generate(String secret) {
        return GOOGLE_AUTHENTICATOR.getTotpPassword(secret);
    }
}