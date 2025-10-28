package io.github.isysdcore.genericAutoCrud.generics;

/**
 * @author domingos.fernando
 * @created 04/01/2025 - 14:57
 * @project smsg
 */
public interface AuthenticationManagement {
    /**
     * Method do prepare and insert in database a new active user
     * with the right privileges to execute de operations to test
     */
    void prepareNewUser();
    /**
     * Method to execute authentication and return a Json Web Token to store on
     * 'bearerJwToken' attribute, other methods will reuse this token injecting on header of
     * each request with prefix 'Bearer', to execute their operations.
     */
    String authenticateUser(String userName, String password) throws Exception;
}
