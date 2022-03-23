package kr.co.won.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FtpConfig {

    private final DataSource dataSource;

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
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        PasswordEncryptor passwordEncryptor = new PasswordEncryptor() {

            @Override
            public String encrypt(String s) {
                log.info("password encoding ::: {}", s);

                return bCryptPasswordEncoder.encode(s);
            }

            @Override
            public boolean matches(String s, String s1) {
                log.info("get password ::: {}, get raw password ::: {}", s1, s);
                Boolean matches = bCryptPasswordEncoder.matches(s1, s);
                log.info("password matching ::: {}", matches);
                return true;
            }
        };
        return passwordEncryptor;
    }

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

    @Bean
    public UserManager userManager() {
        UserManager userManager = userManagerFactory().createUserManager();
        log.info("get user manager :::  {}", userManager);
        return userManager;
    }


    @Bean
    public FtpServerFactory ftpServerFactory() throws FtpException {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        Listener listener = listenerFactory().createListener();
        log.info("get listener ::: {}, get port ::: {}", listener.toString(), listener.getPort());
        // listener setting
        ftpServerFactory.addListener("defaultListener", listener);

        // user manager setting
        UserManager userManager = userManager();
        ftpServerFactory.setUserManager(userManager);

        BaseUser adminUser = new BaseUser();
        adminUser.setName("admin");
        adminUser.setPassword("admin");
        adminUser.setHomeDirectory("/test");
        adminUser.setEnabled(true);

        userManager.save(adminUser);

        return ftpServerFactory;
    }

    @Bean
    public FtpServer ftpServer() throws FtpException {
        FtpServer server = ftpServerFactory().createServer();
        server.start();
        log.info("get ftp server status ::: {}", server.isStopped());
        return server;
    }
}
