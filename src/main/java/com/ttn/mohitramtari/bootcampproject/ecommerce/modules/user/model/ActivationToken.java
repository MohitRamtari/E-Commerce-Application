package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.app.util.GlobalVariables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activation_token")
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public ActivationToken(User user) {
        this.user = user;
        createdDate = new Date();
        activationToken = UUID.randomUUID().toString();
    }

    public boolean isActivationTokenExpired() {
        return ((new Date().getTime() - this.createdDate.getTime()) / 1000)
                >= GlobalVariables.ACTIVATION_TOKEN_VALIDATION_TIME_IN_SECONDS;
    }

    public boolean isResetPasswordTokenExpired() {
        return ((new Date().getTime() - this.createdDate.getTime()) / 1000)
                >= GlobalVariables.RESET_PASSWORD_TOKEN_VALIDATION_TIME_IN_SECONDS;
    }
}
