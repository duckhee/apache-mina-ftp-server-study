package kr.co.won.auth;

import kr.co.won.user.domain.UserDomain;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * this using jpa get domain
 * custom ftp user
 */
public class AuthUser implements User {

    private UserDomain user;
    private List<? extends Authority> authorities = new ArrayList();


    public AuthUser(UserDomain user) {
        this.user = user;
        List<Authority> auths = new ArrayList<>();
        auths.add(new UserAuthority(user));
        this.authorities = Collections.unmodifiableList(auths);

    }

    public AuthUser(UserDomain user, List<Authority> authorities) {
        this.user = user;
        if (authorities != null) {
            this.authorities = Collections.unmodifiableList(authorities);
        } else {
            this.authorities = null;
        }

    }

    @Override
    public String getName() {
        return user.getFtpId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }


    public void setAuthorities(List<Authority> authorities) {
        if (authorities != null) {
            this.authorities = Collections.unmodifiableList(authorities);
        } else {
            this.authorities = null;
        }
    }


    @Override
    public List<? extends Authority> getAuthorities() {
        if (authorities != null) {
            return Collections.unmodifiableList(authorities);
        }
        return null;
    }

    @Override
    public List<? extends Authority> getAuthorities(Class<? extends Authority> aClass) {
        List<Authority> selected = new ArrayList<Authority>();

        for (Authority authority : authorities) {
            if (authority.getClass().equals(aClass)) {
                selected.add(authority);
            }
        }

        return selected;
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        // check for no authorities at all
        // not have auth
        if (authorities == null) {
            return null;
        }
        boolean someoneCouldAuthorize = false;
        for (Authority authority : authorities) {
            if (authority.canAuthorize(authorizationRequest)) {
                someoneCouldAuthorize = true;
                // auth check write permission
                authorizationRequest = authority.authorize(authorizationRequest);
                // authorization failed, return null
                if (authorizationRequest == null) {
                    return null;
                }
            }
        }
        // get auth
        if (someoneCouldAuthorize) {
            return authorizationRequest;
        }
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
