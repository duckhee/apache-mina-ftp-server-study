package kr.co.won.configuration;

import kr.co.won.auth.AuthUserManager;
import kr.co.won.auth.AuthUserManagerFactory;
import kr.co.won.file.persistence.FtpFilePersistence;
import kr.co.won.handler.FtpLetCustom;
import kr.co.won.user.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FtpConfig {


    private final DataSource dataSource;
    private final FtpProperties appProperties;
    private final String ROOT_PATH = "/Users/duckheewon/Desktop/temp";
    private final UserPersistence userPersistence;
    private final FtpFilePersistence ftpFilePersistence;

    @Bean
    public ListenerFactory listenerFactory() {
        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setPort(2323);
        log.info("ftp listener factory ::: {}", listenerFactory);
        return listenerFactory;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // password encoding use spring security
    @Bean
    public PasswordEncryptor passwordEncryptor() {

        PasswordEncryptor passwordEncryptor = new PasswordEncryptor() {
            Md5PasswordEncryptor md5PasswordEncryptor = new Md5PasswordEncryptor();

            @Override
            public String encrypt(String s) {
                log.info("password encoding ::: {}", s);
                String encodePw = md5PasswordEncryptor.encrypt(s);
                log.info("password encoding ::: {}", encodePw);

                return encodePw;
            }

            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
                log.info("get store password ::: {}, get check password ::: {}", storedPassword, passwordToCheck);
                Boolean matches = md5PasswordEncryptor.matches(passwordToCheck, storedPassword);
                log.info("password matching ::: {}", matches);
                return matches;
            }
        };

        return passwordEncryptor;
    }

    /**
     * TODO Example Database User Manager Factory
     */
    @Bean
    public UserManagerFactory userManagerFactory() {
        DbUserManagerFactory managerFactory = new DbUserManagerFactory();
        managerFactory.setAdminName("admin");
        managerFactory.setDataSource(dataSource);

        // password encoder setting
        managerFactory.setPasswordEncryptor(passwordEncryptor());
        // insert user query
        managerFactory.setSqlUserInsert("INSERT INTO tbl_ftp_user (userid, userpassword,\n" +
                "        homedirectory, enableflag, writepermission, idletime, uploadrate,\n" +
                "        downloadrate) VALUES ('{userid}', '{userpassword}', '{homedirectory}',\n" +
                "        '{enableflag}', '{writepermission}', {idletime}, {uploadrate},\n" +
                "        {downloadrate})");
        // update user query
        managerFactory.setSqlUserUpdate(" UPDATE tbl_ftp_user SET\n" +
                "            userpassword='{userpassword}',\n" +
                "            homedirectory='{homedirectory}',\n" +
                "            enableflag={enableflag},\n" +
                "            writepermission={writepermission},\n" +
                "            idletime={idletime},\n" +
                "            uploadrate={uploadrate},\n" +
                "            downloadrate={downloadrate}\n" +
                "        WHERE userid='{userid}'");
        // select user query
        managerFactory.setSqlUserSelect("SELECT userid, userpassword, homedirectory,\n" +
                "            enableflag, writepermission, idletime, uploadrate, downloadrate, maxloginnumber, maxloginperip \n" +
                "        FROM tbl_ftp_user \n" +
                "        WHERE userid = '{userid}'");
        // delete user query
        managerFactory.setSqlUserDelete("DELETE FROM tbl_ftp_user WHERE userid = '{userid}'\n");
        // select user all query
        managerFactory.setSqlUserSelectAll("SELECT userid FROM tbl_ftp_user ORDER BY userid\n");
        // select user auth query
        managerFactory.setSqlUserAdmin("SELECT userid \n" +
                "        FROM tbl_ftp_user \n" +
                "        WHERE userid='{userid}' AND userid='admin'");
        // get user password
        managerFactory.setSqlUserAuthenticate("SELECT userpassword from tbl_ftp_user WHERE userid='{userid}'");
        return managerFactory;
    }

    /**
     * TODO Example User Manager
     */
    @Bean
    public UserManager userManager() {
        UserManager userManager = userManagerFactory().createUserManager();
        log.info("get user manager :::  {}", userManager);
        return userManager;
    }

    /**
     * USE Spring Data JPA
     */
    @Bean
    public AuthUserManagerFactory authUserManagerFactory() {
        AuthUserManagerFactory authUserManagerFactory = new AuthUserManagerFactory(userPersistence, passwordEncryptor());
        return authUserManagerFactory;
    }

    @Bean
    public UserManager authUserManager() {
        UserManager userManager = authUserManagerFactory().createUserManager();
        return userManager;
    }

    @Bean
    public FtpServerFactory ftpServerFactory() throws FtpException {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        Listener listener = listenerFactory().createListener();
        log.info("get listener ::: {}, get port ::: {}", listener.toString(), listener.getPort());
        // listener setting
        ftpServerFactory.addListener("defaultListener", listener);

        // sql user manager
//        UserManager userManager = userManager();
        UserManager userManager = authUserManager();
        // user manager setting
        ftpServerFactory.setUserManager(userManager);

        // make file event map
        Map<String, Ftplet> ftpSupportList = new HashMap<>();
        ftpSupportList.put("default", new FtpLetCustom(userManager, userPersistence, ftpFilePersistence));
        //setting file event handler
        ftpServerFactory.setFtplets(ftpSupportList);

        //TODO  sample user create
        // user role make
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        // make user dir
        File homeDir = new File(appProperties.getRootPath() + "/admin");
        // home dir check
        if (!homeDir.exists()) {
            // make home dir
            homeDir.mkdir();
            log.info("create home dir ::: {}, exists ::: {}", homeDir, homeDir.exists());
        }

        // User admin User make
        BaseUser adminUser = new BaseUser();
        adminUser.setName("admin");
        adminUser.setPassword("admin");
        adminUser.setHomeDirectory(appProperties.getRootPath());
//        adminUser.setHomeDirectory(homeDir.getName());
        adminUser.setEnabled(true);
        adminUser.setAuthorities(authorities);
        userManager.save(adminUser);

        return ftpServerFactory;
    }

    @Bean
    public FtpServer ftpServer() throws FtpException {
        FtpServer server = ftpServerFactory().createServer();
        log.info("get ftp server status ::: {}", server.isStopped());
        log.info("get ftp server status ::: {}", server.isSuspended());
        // ftp server start set
        if (!server.isStopped()) {
            server.stop();
        }
        // ftp server not have handler setting resume handler setting
        if (!server.isSuspended()) {
            server.resume();
        }
        server.start();
        log.info("get ftp server status ::: {}", server.isStopped());
        log.info("get ftp server status ::: {}", server.isSuspended());

        return server;
    }
}
