package com.example.fyp;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AdminUser")
public class AdminUser extends User {
    
}
