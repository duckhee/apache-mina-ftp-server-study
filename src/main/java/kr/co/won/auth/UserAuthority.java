package kr.co.won.auth;

import kr.co.won.user.domain.UserDomain;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

public class UserAuthority implements Authority {

    private String permissionRoot;

    public UserAuthority(UserDomain user) {
        this.permissionRoot = user.getHomePath();
    }

    @Override
    public boolean canAuthorize(final AuthorizationRequest authorizationRequest) {
        return authorizationRequest instanceof WriteRequest;
    }

    @Override
    public AuthorizationRequest authorize(final AuthorizationRequest authorizationRequest) {
        if (authorizationRequest instanceof WriteRequest) {
            WriteRequest writeRequest = (WriteRequest) authorizationRequest;
            String getFile = writeRequest.getFile();
            // get check file path start home permission
            if (getFile.startsWith(permissionRoot)) {
                return writeRequest;
            }
        }
        return null;
    }
}
