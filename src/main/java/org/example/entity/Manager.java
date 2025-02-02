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

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
