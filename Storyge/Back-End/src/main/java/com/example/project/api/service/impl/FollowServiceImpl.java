package com.example.project.api.service.impl;

import com.example.project.api.service.FollowService;
import com.example.project.api.service.NotificationService;
import com.example.project.db.entity.Follow;
import com.example.project.db.entity.FollowWait;
import com.example.project.db.entity.User;
import com.example.project.db.repository.FollowRepository;
import com.example.project.db.repository.FollowWaitRepository;
import com.example.project.db.repository.UserRepository;
import com.example.project.dto.common.FollowUserInfoDto;
import com.example.project.dto.common.NotificationFollowDto;
import com.example.project.dto.common.UserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final FollowWaitRepository followWaitRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 팔로우 신청
    @Override
    public Boolean insertFollowWait(Long userId, UserIdDto following) {

        /*
            userId: 나
            following(followingUserId): 상대 - 내가 신청을 보낼 사람
            db: following이 신청하려는 상대 userId가 현재 신청하는 사람(나)가 되어야 한다
         */

        Long followingUserId = following.getUserId(); // 팔로우 신청을 보낼 사람의 userId

        Optional<User> followingUser = userRepository.findById(followingUserId); // 팔로우 신청할 사람

        if (followingUser.isEmpty())
            return false;


        Optional<FollowWait> checkExist = followWaitRepository.findByFollowingAndAndUserId(followingUserId, userId); // 이미 신청 내역이 존재하는지 확인
        if (checkExist.isPresent())  // 존재하면 false 반환
            return false;
        else { // 신청 내역이 존재하지 않으면
            if (followRepository.findByFollowingAndFollower(followingUserId, userId).isPresent()) { // 팔로우 중인지 확인
                return false; // 팔로우 중이면 false 반환
            } else {// 신청 상태도 아니고 팔로우 중도 아니라면 팔로우 신청 보냄
                followWaitRepository.save(FollowWait.builder()
                        .userId(userId)
                        .following(followingUserId)
                        .build());

                // 알림 table에 insert
                notificationService.insertFollowWaitNotification(
                        NotificationFollowDto.builder()
                                .userId(followingUserId) // 이 사람에게 알림이 간다
                                .follow(userId) // 이 사람이 알림을 보냄
                                .build());

                return true;
            }
        }

    }

    // 팔로우 수락
    @Override
    public Boolean insertFollower(Long userId, UserIdDto follower) {
        
        /*
            userId: 나
            follower: 상대 - 수락 하려는 상대
            db: following이 나 follower에 상대가 들어가야 한다
         */

        Long followerUserId = follower.getUserId(); // 상대방 user_id

        Optional<User> followerUser = userRepository.findById(followerUserId); // 팔로우 신청한 사람

        if (followerUser.isEmpty())
            return false;


        if (followRepository.findByFollowingAndFollower(userId, followerUserId).isPresent()) // 이미 상대가 팔로우 중임
            return false;


        // 대기 상태에서 삭제
        followWaitRepository.deleteByFollowingAndUserId(userId, followerUserId);

        // follow table에 insert (팔로우 수락)
        followRepository.save(Follow.builder()
                .following(userId)
                .follower(followerUserId)
                .build());

        // 팔로우 수락 알림 -> 알림 table에 insert
        notificationService.insertFollowNotification(
                NotificationFollowDto.builder()
                        .userId(followerUserId) // 이사람에게
                        .follow(userId) // 이 사람이 수락했다고 알림이 간다
                        .build());

        return true;
    }

    // 팔로우 대기 목록
    @Override
    public List<FollowUserInfoDto> selectAllFollowWait(Long userId) {


        /*
            내가 following에 있는 데이터들의 리스트
            userId에는 나에게 신청을 건 상대가 있다
         */


        // 나에게 팔로우 신청을 건 사람들을 찾는다
//        List<FollowWait> followWaitList = followWaitRepository.findAllByFollowing(userId); // 팔로우 대기중인 사람들 목록 가져오기
        //        List<FollowUserInfoDto> followWaitUserList = new ArrayList<>(); // 신청한 사람들의 정보를 찾아서 담음

//        for (FollowWait follow : followWaitList) {
//
//            User user = follow.getUser(); // 상대방
//
//            // 나에게 팔로우 신청을 한 사람들 userId, 프로필 사진, nickname 반환
//
//            followWaitUserList.add(UserToFollowUserInfoDto(user));
//        }

        return followWaitRepository.findAllByFollowing(userId).stream().map(this::waitToDto).collect(Collectors.toList());
    }

    //내 팔로잉 목록
    @Override
    public List<FollowUserInfoDto> selectAllFollowing(Long userId) {

        /*
            내가 follower에 있는 데이터들의 리스트
            상대방이 following에 있다
         */

        //내가 팔로워인 사람들 리스트 가져오기
//        List<Follow> followingList = followRepository.findAllByFollower(userId); //내 팔로잉 목록 가져오기
//        List<FollowUserInfoDto> followerUserList = new ArrayList<>(); // 팔로잉들의 정보만 찾아서 담기
//
//        for (Follow follow : followingList) {
//
//            User user = follow.getFollowingUsers();
//
//            // 팔로우중인 사람들 userId, 프로필 사진, nickname 반환
//            followerUserList.add(UserToFollowUserInfoDto(user));
//        }

        return followRepository.findAllByFollower(userId).stream().map(this::followingToDto).collect(Collectors.toList());
    }

    //내 팔로워 목록
    @Override
    public List<FollowUserInfoDto> selectAllFollower(Long userId) {

        /*
            내가 following에 있는 데이터들의 리스트
            상대방은 follower에 있다
            
         */

        //내가 팔로잉에 있는 리스트, 나를 팔로우 하는 사람들을 찾는다
//        List<Follow> followingList = followRepository.findAllByFollowing(userId);
//        List<FollowUserInfoDto> followerUserList = new ArrayList<>(); // 팔로워들의 정보를 담는다
//
//        for (Follow follow : followingList) {
//
//            User user = follow.getFollowerUsers();
//
//            // 팔로워인 사람들 userId, 프로필 사진, nickname 반환
//            followerUserList.add(UserToFollowUserInfoDto(user));
//        }


        return followRepository.findAllByFollowing(userId).stream().map(this::followerToDto).collect(Collectors.toList());
    }

    // 팔로잉 확인
    @Override
    public Boolean checkFollow(Long myId, Long userId) {
//        Follow follow = followRepository.findByFollowingAndFollower(userId, myId);
        return followRepository.findByFollowingAndFollower(userId, myId).isPresent();
    }

    //팔로우 대기 상태 확인
    @Override
    public Boolean checkFollowWait(Long userId, Long otherId) {
        return followWaitRepository.findByFollowingAndAndUserId(otherId, userId).isPresent();
    }

    //팔로우 거절(대기 상태 삭제)
    @Override
    public Boolean deleteFollowWait(Long userId, Long follow) {
        /*
            userId : 나
            follow: 삭제할 사람
            db: 신청한 사람이 userId 신청 받은 사람이 following
            삭제 하기 위해서는 deleteByFollowingAndUserId에서
            following이 내가 되어야 하고
            userId가 나에게 신청을 한 사람, 즉 신청 내역에서 삭제할 사람의 번호가 되어야 한다
         */

        if (userId != null && follow != null) {
            followWaitRepository.deleteByFollowingAndUserId(userId, follow);
            return true;
        }
        return false;
    }

    //언팔로우 하기
    @Override
    public Boolean deleteFollow(Long following, Long follower) {

        /*
            userId: 나, follow: 상대방(내가 팔로우 하는 사람)
            내가 follower 지우려는 상대가 following
         */
//
//        Optional<User> selectFollowing = userRepository.findById(following); // 지우려는 상대: following
//
//        if (selectFollowing.isEmpty()) {
//            return false;
//        }

        if (followRepository.findByFollowingAndFollower(following, follower).isEmpty())// 만약 팔로우 중이 아니거나 상대가 먼저 삭제 해버림
            return false;


        followRepository.deleteByFollowingAndFollower(following, follower);

        return true;
    }


    //팔로워 삭제하기
//    @Override
//    public Boolean deleteFollower(Long userId, Long follow) {
//
//        /*
//            내가 following 지우려는 상대가 follower
//         */
//
//        User selectFollower = userRepository.findById(follow).orElse(null); // 지우려는 상대: follower
//
//        if (selectFollower == null) {
//            return false;
//        }
//
//        if (followRepository.findByFollowingAndFollower(userId, follow) == null) { // 팔로워가 아니거나 상대가 먼저 언팔로우
//            return false;
//        }
//
//        followRepository.deleteByFollowingAndFollower(userId, follow);
//
//        return true;
//
//    }


}

