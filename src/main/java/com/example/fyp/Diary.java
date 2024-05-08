package com.example.fyp;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // getters and setters
@AllArgsConstructor // class constructor
@NoArgsConstructor // default constructor
@Table(name = "diary")
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto generate id at db end
    @Column(name = "diary_id")
    private int diary_id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "input_text")
    private String input_text;

    @Column(name = "emotional_intensity")
    private String emotional_intensity;

    @Column(name = "overall_sentiment")
    private String overall_sentiment;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL) // mapped to diary attribute in TargetEmotion class
    @JsonIgnore // to prevent infinite relationship loop
    private List<TargetEmotion> targetEmotionsList;

}
