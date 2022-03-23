package kr.co.won.auth;

import kr.co.won.user.domain.UserDomain;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

import java.util.List;

/**
 * this using jpa get domain
 */
public class AuthUser implements User {

    private UserDomain user;

    public AuthUser(UserDomain user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getFtpId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public List<? extends Authority> getAuthorities() {
        return null;
    }

    @Override
    public List<? extends Authority> getAuthorities(Class<? extends Authority> aClass) {
        return null;
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        return null;
    }

    @Override
    public int getMaxIdleTime() {
        return 0;
    }

    @Override
    public boolean getEnabled() {
        return user.isUsed();
    }

    @Override
    public String getHomeDirectory() {
        return user.getHomePath();
    }
}
