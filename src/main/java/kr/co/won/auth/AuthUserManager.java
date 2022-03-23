package kr.co.won.auth;

import kr.co.won.user.domain.UserDomain;
import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;

@Slf4j
@RequiredArgsConstructor
public class AuthUserManager extends AbstractUserManager {

    private UserPersistence userPersistence;


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
        return new String[0];
    }

    @Override
    public void delete(String s) throws FtpException {

    }

    @Override
    public void save(User user) throws FtpException {

    }

    @Override
    public boolean doesExist(String s) throws FtpException {
        return false;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        return null;
    }
}
