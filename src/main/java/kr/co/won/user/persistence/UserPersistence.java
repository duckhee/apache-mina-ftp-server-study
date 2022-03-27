package kr.co.won.user.persistence;

import kr.co.won.user.domain.ftp.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPersistence extends JpaRepository<UserDomain, Long> {

    /**
     * ftp id find user
     */
    Optional<UserDomain> findByFtpId(String ftpId);

    /** ftp id user exists */
    public boolean existsByFtpId(String ftpId);
}
