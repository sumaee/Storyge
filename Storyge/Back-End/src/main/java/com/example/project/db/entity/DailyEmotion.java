package com.example.project.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
@Getter
public class DailyEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private String emoticonName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public void updateDailyEmotion(String emoticonName) {
        this.emoticonName = emoticonName;
    }

}
