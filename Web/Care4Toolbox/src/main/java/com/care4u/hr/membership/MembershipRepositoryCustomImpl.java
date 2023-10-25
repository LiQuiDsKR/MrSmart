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

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.care4u.hr.part.PartDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class MembershipRepositoryCustomImpl  implements MembershipRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MembershipRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    
    private BooleanExpression searchRoleEquals(Role searchRole){
        return searchRole == null ? null : QMembership.membership.role.eq(searchRole);
    }
    
    private BooleanExpression searchEmploymentStateEquals(EmploymentState searchEmploymentState){
        return searchEmploymentState == null ? null : QMembership.membership.employmentState.eq(searchEmploymentState);
    }
    
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("id", searchBy)){
            return QMembership.membership.id.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("name", searchBy)){
            return QMembership.membership.name.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("code", searchBy)){
            return QMembership.membership.code.like("%" + searchQuery + "%");
        }

        return null;
    }

	@Override
	public Page<Membership> getMembershipPage(MembershipSearchDto membershipSearchDto, Pageable pageable) {

        List<Membership> content = queryFactory
                .selectFrom(QMembership.membership)
                .where(searchRoleEquals(membershipSearchDto.getSearchRole()),
                		searchEmploymentStateEquals(membershipSearchDto.getSearchEmploymentState()),
                        searchByLike(membershipSearchDto.getSearchBy(),
                                membershipSearchDto.getSearchQuery()))
                .orderBy(QMembership.membership.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        long total = queryFactory.select(Wildcard.count).from(QMembership.membership)
                .where(searchRoleEquals(membershipSearchDto.getSearchRole()),
                		searchEmploymentStateEquals(membershipSearchDto.getSearchEmploymentState()),
                        searchByLike(membershipSearchDto.getSearchBy(),
                                membershipSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
	}
}
