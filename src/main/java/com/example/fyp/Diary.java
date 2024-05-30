package com.example.fyp;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private int diaryId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "input_text")
    private String inputText;

    @Column(name = "emotional_intensity")
    private String emotionalIntensity;

    @Column(name = "overall_sentiment")
    private String overallSentiment;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL) // mapped to diary attribute in TargetEmotion class
    @JsonIgnore // to prevent infinite relationship loop
    private List<TargetEmotion> targetEmotionsList;

    @ManyToOne(fetch = FetchType.LAZY) // mapped to diaries attribute in User class
    @JoinColumn(name = "user_id")
    @JsonIgnore // to prevent infinite relationship loop
    private User user;

}
