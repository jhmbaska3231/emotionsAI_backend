package com.example.fyp;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("FreeUser")
@Data
@EqualsAndHashCode(callSuper = false) // tell Lombok not to include the superclass fields in the equals and hashCode implementations
public class FreeUser extends User {
    
    @Column(name = "transcribe_count")
    private int transcribe_count;

    @Column(name = "last_transcribe_time")
    private LocalDateTime last_transcribe_time;

}
