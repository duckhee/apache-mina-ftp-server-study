package kr.co.won.user.domain.ftp;

import kr.co.won.user.domain.AuthTypeEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString(exclude = {})
@EqualsAndHashCode(of = {"idx"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_ftp_user_domain")
public class UserDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true)
    private String ftpId;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean used = true;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthTypeEnum role = AuthTypeEnum.USER;

    @Column(nullable = false)
    private String homePath;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** user ftp domain function */


}
