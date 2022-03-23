package kr.co.won.auth;

import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;

@RequiredArgsConstructor
public class AuthUserManagerFactory extends DbUserManagerFactory {

    private final UserPersistence userPersistence;
}
