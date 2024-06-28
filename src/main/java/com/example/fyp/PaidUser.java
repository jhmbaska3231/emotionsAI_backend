package com.example.fyp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("PaidUser")
@Data
@EqualsAndHashCode(callSuper = true)
public class PaidUser extends User {

    @OneToOne(mappedBy = "paidUser", cascade = CascadeType.ALL)
    @JsonManagedReference // another way to prevent infinite relationship loop, owner side of the relationship use "JsonManagedReference"
    private Subscription subscription;

}
