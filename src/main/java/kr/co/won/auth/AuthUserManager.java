package kr.co.won.auth;

import kr.co.won.user.domain.UserDomain;
import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AuthUserManager extends AbstractUserManager {

    private final UserPersistence userPersistence;
    private final PasswordEncryptor passwordEncryptor;


    @Override
    public User getUserByName(String ftpUserId) throws FtpException {
        UserDomain findUser = userPersistence.findByFtpId(ftpUserId).orElse(null);
        if(findUser == null){
            log.info("not have user");
        }
        return null;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        // find all user
        return (String[]) userPersistence.findAll().stream().map(UserDomain::getFtpId).toArray();
    }

    @Override
    public void delete(String s) throws FtpException {
        log.info("delete user not support");
//        UserDomain userDomain = userPersistence.findByFtpId(s).orElse(null);
//        // user delete logic
//        if(userDomain != null)
//            userPersistence.delete(userDomain);
    }

    @Override
    public void save(User user) throws FtpException {
        // TODO Setting user save not support
        AuthUser newUser = (AuthUser) user;
        // get user domain
        String password = newUser.getPassword();
        log.info("get password ::: {}", password);
        UserDomain newDomain = UserDomain.builder()
                .ftpId(newUser.getName())
                .password(passwordEncryptor.encrypt(password))
                .homePath(newUser.getHomeDirectory())
                .used(true)
                .build();
        UserDomain savedUser = userPersistence.save(newDomain);
        log.info("get user saved ::: {}", savedUser);
    }

    @Override
    public boolean doesExist(String s) throws FtpException {
        return userPersistence.existsByFtpId(s);
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        return null;
    }
}
