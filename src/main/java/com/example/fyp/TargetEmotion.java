package com.example.fyp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // getters and setters
@AllArgsConstructor // class constructor
@NoArgsConstructor // default constructor
@Table(name = "target_emotion")
public class TargetEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto generate id at db end
    private int id;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "emotion_percentage")
    private Double emotion_percentage;

}
