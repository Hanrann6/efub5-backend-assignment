package efub.assignment.community.member.repository;

import efub.assignment.community.member.domain.Member;

import java.util.List;

// QueryDsl 인터페이스
public interface CustomMemberRepository {
    List<Member> findMembersByNickname(String nickname);
}
