package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "manager")
public class Manager implements Serializable {
    @Id
   // @Column(name = "managerId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int managerId;

    @Column(name = "managerName")
   // @Column(nullable = false, length = 50)
    private String managerName;

    @Column(name = "managerPassword")
    //@Column(nullable = false, length = 50)
    private String managerPassword;

}
