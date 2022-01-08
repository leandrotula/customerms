package com.app.customer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class CustomerEntity {

  @Id
  private Long id;

  private String name;

  private String surname;

  private String photoUrl;

  private String status;

  @OneToOne
  @JoinColumn(name="USER_ID")
  private UserEntity userEntity;

}
