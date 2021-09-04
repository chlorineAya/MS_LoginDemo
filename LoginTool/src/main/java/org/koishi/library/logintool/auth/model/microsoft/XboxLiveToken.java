package org.koishi.library.logintool.auth.model.microsoft;

import org.koishi.library.logintool.auth.abstracts.AuthenticationToken;

/**
 * The class Xbox live token stores token and uhs
 */
public class XboxLiveToken extends AuthenticationToken {

    protected String token;
    protected String uhs;

    /**
     * Instantiates a new Xbox live token.
     */
    public XboxLiveToken() {
    }

    /**
     * Instantiates a new Xbox live token.
     *
     * @param token the token
     * @param uhs   the uhs
     */
    public XboxLiveToken(String token, String uhs) {
        this.token = token;
        this.uhs = uhs;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets uhs.
     *
     * @return the uhs
     */
    public String getUhs() {
        return uhs;
    }


}
