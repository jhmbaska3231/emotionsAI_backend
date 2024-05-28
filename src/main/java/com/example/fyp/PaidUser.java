package com.example.fyp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("PaidUser")
public class PaidUser extends User {

    @OneToOne(mappedBy = "paidUser", cascade = CascadeType.ALL)
    private Subscription subscription;

}
