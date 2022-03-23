package kr.co.won.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.ftplet.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class FtpLetCustom extends DefaultFtplet {

    private final UserManager userManager;

    @Override
    public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
        String name = session.getUser().getName();
        log.info("get on login session ::: {}, user name ::: {}", session, name);
        return super.onLogin(session, request);
    }

    @Override
    public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
        log.info("get connection session ::: {}", session);
        return super.onConnect(session);
    }

    @Override
    public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
        log.info("get dis connection session ::: {}", session);
        return super.onDisconnect(session);
    }

    @Override
    public FtpletResult onUploadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get upload start session ::: {}", session);
        log.info("get upload start request ::: {}", request);
        // session get user name
        String findUserName = session.getUser().getName();
        if (!userManager.doesExist(findUserName)) {
            return FtpletResult.DISCONNECT;
        }
        return super.onUploadStart(session, request);
    }

    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get upload end session ::: {}", session);
        log.info("get upload end request ::: {}", request);
        return super.onUploadEnd(session, request);
    }

    @Override
    public FtpletResult onDownloadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get download start session ::: {}", session);
        return super.onDownloadStart(session, request);
    }

    @Override
    public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get download end session ::: {}", session);
        return super.onDownloadEnd(session, request);
    }

    @Override
    public FtpletResult onRmdirStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get rmdir start session ::: {}, request ::: {}", session, request);
        return super.onRmdirStart(session, request);
    }

    @Override
    public FtpletResult onRmdirEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get rmdir end session ::: {}, request ::: {}", session, request);
        return super.onRmdirEnd(session, request);
    }

    @Override
    public FtpletResult onMkdirStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get mkdir start session ::: {}, request ::: {}", session, request);
        return super.onMkdirStart(session, request);
    }

    @Override
    public FtpletResult onMkdirEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get mkdir end session ::: {}, request ::: {}", session, request);
        return super.onMkdirEnd(session, request);
    }

    @Override
    public FtpletResult onAppendStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get append start session ::: {}, request ::: {}", session, request);
        return super.onAppendStart(session, request);
    }

    @Override
    public FtpletResult onAppendEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get append end session ::: {}, request ::: {}", session, request);
        return super.onAppendEnd(session, request);
    }

    @Override
    public FtpletResult onUploadUniqueStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get unique start session ::: {}, request ::: {}", session, request);
        return super.onUploadUniqueStart(session, request);
    }

    @Override
    public FtpletResult onUploadUniqueEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get unique end session ::: {}, request ::: {}", session, request);
        return super.onUploadUniqueEnd(session, request);
    }

    @Override
    public FtpletResult onRenameStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get rename start session ::: {}, request ::: {}", session, request);
        return super.onRenameStart(session, request);
    }

    @Override
    public FtpletResult onRenameEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        log.info("get rename end session ::: {}, request ::: {}", session, request);
        return super.onRenameEnd(session, request);
    }
}
