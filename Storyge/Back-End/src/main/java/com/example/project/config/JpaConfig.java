package com.example.project.config;

import com.example.project.api.service.*;
import com.example.project.api.service.impl.*;
import com.example.project.db.repository.*;
import com.example.project.db.repository.custom.DiaryCustomRepository;
import com.example.project.db.repository.custom.RecentDiaryCustomRepository;
import com.example.project.user.model.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final DailyEmotionRepository dailyEmotionRepository;

    private final DiaryRepository diaryRepository;
    private final DiaryCustomRepository diaryCustomRepository;
    private final DiaryCountRepository diaryCountRepository;
    private final NotificationRepository notificationRepository;
    private final QuoteRepository quoteRepository;
    private final TodayQuoteRepository todayQuoteRepository;
    private final RecentDiaryRepository recentDiaryRepository;
    private final ReadDiaryRepository readDiaryRepository;
    private final RecentDiaryCustomRepository recentDiaryCustomRepository;
    private final ReviewRepository reviewRepository;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository, followRepository);
    }

    @Bean
    public DailyEmotionService dailyEmotionService() {
        return new DailyEmotionServiceImpl(dailyEmotionRepository, userRepository);
    }

    @Bean
    public DiaryService diaryService() {
        return new DiaryServiceImpl(diaryRepository, diaryCustomRepository, userRepository, recentDiaryRepository, diaryCountRepository, dailyEmotionService(), recentDiaryService());
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationServiceImpl(notificationRepository);
    }

    @Bean
    public QuoteService quoteService() {
        return new QuoteServiceImpl(quoteRepository, todayQuoteRepository);
    }

    @Bean
    public RecentDiaryService recentDiaryService() {
        return new RecentDiaryServiceImpl(recentDiaryRepository, readDiaryRepository, followRepository, recentDiaryCustomRepository, diaryRepository);
    }

    @Bean
    public ReviewService reviewService() {
        return new ReviewServiceImpl(reviewRepository, diaryRepository, notificationService());
    }

}
