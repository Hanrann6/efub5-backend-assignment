package efub.assignment.community.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersByNickname(String nickname) {

        QMember member = QMember.member;

        // BooleanBuilder 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 동적 쿼리 조건
        if (nickname != null && !nickname.isBlank()) {
            builder.and(member.nickname.containsIgnoreCase(nickname));
        }

        // 쿼리 실행
        return queryFactory
                .selectFrom(member)
                .where(builder) // builder를 where 조건으로 사용
                .orderBy(member.memberId.desc()) // 정렬 순서 추가
                .fetch();
    }
}
