package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

/**
 * Testing utilities for IR classes.
 */

public class IrUtils {

    public static LoginIr loginSuccess() {
        LoginIr ir = new LoginIr();
        ir.tokenType = "bearer";
        ir.accessToken = "9cd7d634-fd3f-4b60-b96a-994733d72a19";
        ir.expiresIn = 10580;
        ir.refreshToken = "d87e6156-b5fc-49b8-9b1c-45c4e3b48607";
        return ir;
    }
}
