package kr.co.won.auth;

import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.UserManagerFactory;

@RequiredArgsConstructor
public class AuthUserManagerFactory implements UserManagerFactory {

    private final UserPersistence userPersistence;

    @Override
    public UserManager createUserManager() {
        return null;
    }
}
