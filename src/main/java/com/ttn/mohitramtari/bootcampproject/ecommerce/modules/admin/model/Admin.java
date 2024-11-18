package com.ttn.mohitramtari.bootcampproject.ecommerce.modules.admin.model;

import com.ttn.mohitramtari.bootcampproject.ecommerce.modules.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "admin")
public class Admin extends User {

}
