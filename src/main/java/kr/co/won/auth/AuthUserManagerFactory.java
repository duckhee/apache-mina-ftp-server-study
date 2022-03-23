package kr.co.won.auth;

import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UserManagerFactory;

// TODO change password encoder
@RequiredArgsConstructor
public class AuthUserManagerFactory implements UserManagerFactory {

    private final UserPersistence userPersistence;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public UserManager createUserManager() {
        if (userPersistence == null) {
            throw new RuntimeException("user persistence not have.");
        }
        return new AuthUserManager(userPersistence, passwordEncryptor);
    }
}
