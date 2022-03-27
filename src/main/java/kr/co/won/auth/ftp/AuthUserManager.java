package kr.co.won.auth.ftp;

import com.querydsl.core.util.StringUtils;
import kr.co.won.user.domain.UserDomain;
import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AuthUserManager implements UserManager {

    ///////////////////////

    ///////////////////////

    private final UserPersistence userPersistence;
    private final PasswordEncryptor passwordEncryptor;


    @Override
    public User getUserByName(String ftpUserId) throws FtpException {
        UserDomain findUser = userPersistence.findByFtpId(ftpUserId).orElse(null);
        if (findUser == null) {
            log.info("not have user");
        }
        AuthUser authUser = new AuthUser(findUser);
        log.info("jpa style auth User ::: {}", authUser);
        // TODO Example base User
        BaseUser defaultUser = new BaseUser();
        defaultUser.setName(findUser.getFtpId());
        defaultUser.setPassword(findUser.getPassword());
        defaultUser.setHomeDirectory(findUser.getHomePath());
        defaultUser.setEnabled(findUser.isUsed());
        // TODO Check what is ???
        defaultUser.setMaxIdleTime(0);
        List<Authority> authorities = new ArrayList<>();
        // TODO Permission set
        // writePermission setting
        authorities.add(new WritePermission());
        // need to this check point
        authorities.add(new ConcurrentLoginPermission(0, 0));
        authorities.add(new TransferRatePermission(0, 0));

        // user role setting
        defaultUser.setAuthorities(authorities);
        // custom user set auth
        authUser.setAuthorities(authorities);
        return authUser;
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
        User newUser = user;
        log.info("get save user information ::: {}", user.toString());
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



    /** Login user check auth */
    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        log.info("get auth ::: {}", authentication);
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication userAuth = (UsernamePasswordAuthentication) authentication;
            // authentication get ID and password
            String userName = userAuth.getUsername();
            String userPw = userAuth.getPassword();
            UserDomain findUser = userPersistence.findByFtpId(userName).orElseThrow(() -> new AuthenticationFailedException("Authentication failed."));
            // userPw is null and empty set blink
            if (StringUtils.isNullOrEmpty(userPw)) {
                userPw = "";
            }
            if (passwordEncryptor.matches(userPw, findUser.getPassword())) {
                try {
                    return getUserByName(findUser.getFtpId());
                } catch (FtpException e) {
                    throw new AuthenticationFailedException("Authentication failed", e);
                }
            }
        }
        if (authentication instanceof AnonymousAuthentication) {
            log.info("user anonymous.");
            throw new AuthenticationFailedException("Authentication failed.");
        }
        // not match ftp user
        throw new AuthenticationFailedException("Authentication wrong type ");
    }

    @Override
    public String getAdminName() throws FtpException {
        return null;
    }

    @Override
    public boolean isAdmin(String username) throws FtpException {
        return false;
    }

    /**
     * Escape string to be embedded in SQL statement.
     */
    private String escapeString(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder valBuf = new StringBuilder(input);
        for (int i = 0; i < valBuf.length(); i++) {
            char ch = valBuf.charAt(i);
            if (ch == '\'' || ch == '\\' || ch == '$' || ch == '^' || ch == '['
                    || ch == ']' || ch == '{' || ch == '}') {

                valBuf.insert(i, '\\');
                i++;
            }
        }
        return valBuf.toString();
    }
}
