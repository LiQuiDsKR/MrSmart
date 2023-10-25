/**
 * 2023-10-25 박경수
 * search & paging 기능 테스트를 위해 추가했습니다
 */

package com.care4u.hr.membership;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class MembershipRepositoryCustomImpl  implements MembershipRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MembershipRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

	@Override
	public Page<MembershipDto> getMembershipPage(MembershipSearchDto membershipSearchDto, Pageable pageable) {
		QMembership membership = QMembership.membership;
        //QMembershipImg membershipImg = QMembershipImg.membershipImg;
		//이미지 추가하고 싶다면 주석 해제 후 구현하기

        List<MembershipDto> content = queryFactory
                .select(
                        new QMembershipDto(
                        		membership.id,
                        		membership.name,
                        		membership.code,
                        		//membershipImg.imgUrl, 이미지
                        		membership.password,
                        		membership.part
                        		membership.role,
                        		membership.employmentState)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
	}
}
