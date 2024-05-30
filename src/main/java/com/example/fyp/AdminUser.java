package com.example.fyp;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("AdminUser")
@Data
@EqualsAndHashCode(callSuper = true) // tell Lombok not to include the superclass fields in the equals and hashCode implementations
public class AdminUser extends User {
    
}
