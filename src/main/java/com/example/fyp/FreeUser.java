package com.example.fyp;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FreeUser")
public class FreeUser extends User {
    
}
