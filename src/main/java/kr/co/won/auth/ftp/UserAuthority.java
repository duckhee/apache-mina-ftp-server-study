package kr.co.won.auth.ftp;

import kr.co.won.user.domain.UserDomain;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

public class UserAuthority implements Authority {

    // permission user home dir
    private String permissionRoot;

    public UserAuthority() {
        this.permissionRoot = "/";
    }

    public UserAuthority(String permissionRoot) {
        this.permissionRoot = permissionRoot;
    }

    public UserAuthority(UserDomain user) {
        this.permissionRoot = user.getHomePath();
    }

    // this auth is user request check
    @Override
    public boolean canAuthorize(final AuthorizationRequest authorizationRequest) {
        return authorizationRequest instanceof WriteRequest;
    }

    // this auth is check
    // file upload path check
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
