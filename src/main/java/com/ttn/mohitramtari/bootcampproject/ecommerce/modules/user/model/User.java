package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.Auditable;
import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.role.model.Role;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_first_name")
    private String userFirstName;

    @Column(name = "user_middle_name")
    private String userMiddleName;

    @Column(name = "user_last_name")
    private String userLastName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Role role;

    @Column(name = "user_is_deleted")
    private Boolean userIsDeleted;

    @Column(name = "user_is_active")
    private Boolean userIsActive;

    @Column(name = "user_is_expired")
    private Boolean userIsExpired;

    @Column(name = "user_is_locked")
    private Boolean userIsLocked;

    @Column(name = "user_invalid_attempt_count")
    private Integer userInvalidAttemptCount;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_password_update_date")
    private LocalDate userPasswordUpdateDate = LocalDate.now();
}
