package kr.co.won.file.persistence;

import kr.co.won.file.domain.FileDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FtpFilePersistence extends JpaRepository<FileDomain, Long> {
}
