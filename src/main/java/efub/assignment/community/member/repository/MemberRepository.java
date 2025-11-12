package efub.assignment.community.member.repository;

import efub.assignment.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
    // 이메일 중복검사를 위한 쿼리
    boolean existsByStudentId(String studentId);
    // 학번 중복검사를 위한 쿼리
    boolean existsByEmail(String email);
    // 멤버 ID로 조회
    Optional<Member> findByMemberId(Long memberId);
}
