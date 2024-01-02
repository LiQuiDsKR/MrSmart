/**
 * 2023-10-25 박경수
 * search & paging 기능 테스트를 위해 추가했습니다
 */

package com.care4u.toolbox.tool;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import com.care4u.constant.EmploymentState;
import com.care4u.constant.Role;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ToolRepositoryCustomImpl implements ToolRepositoryCustom {

	private JPAQueryFactory queryFactory;

	public ToolRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	@Override
	public Page<Tool> findByNameContaining(Pageable pageable, String name) {
		QTool membership = QTool.tool;

		String[] keywords = name.split(" "); // 검색어를 공백 기준으로 분리
		List<BooleanExpression> conditions = new ArrayList<>();

		for (String keyword : keywords) {
			BooleanExpression condition = membership.name.contains(keyword);
			conditions.add(condition);
		}

		BooleanExpression finalCondition = Expressions.anyOf(conditions.toArray(new BooleanExpression[0]));

		List<Tool> content = queryFactory.selectFrom(QTool.tool).where(finalCondition)
				.orderBy(QTool.tool.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
				.fetch();

		long total = queryFactory.select(Wildcard.count).from(QTool.tool).where(finalCondition).fetchOne();

		return new PageImpl<>(content, pageable, total);
	}
}
