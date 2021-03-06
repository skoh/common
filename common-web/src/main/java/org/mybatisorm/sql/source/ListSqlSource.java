/*
 *    Copyright 2012 The MyBatisORM Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatisorm.sql.source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.mybatisorm.Query;

import com.nemustech.common.util.StringUtil;
import com.nemustech.common.util.Utils;

public class ListSqlSource extends AbstractSelectSqlSource {

	private static Log logger = LogFactory.getLog(ListSqlSource.class);
	
	public ListSqlSource(SqlSourceBuilder sqlSourceParser, Class<?> clazz) {
		super(sqlSourceParser, clazz);
	}

	public BoundSql getBoundSql(final Object queryParam) {
		Query query = (Query)queryParam;

		// 필드, 힌트, 테이블 추가 by skoh
		staticSql = makeSelectSql(staticSql, query);

		// 주석 처리 by skoh
//		String where = null;
		StringBuilder sb = new StringBuilder(staticSql);
		// 모든 조건 적용 by skoh
//		where = query.hasCondition() ? query.getCondition() :
//			handler.getNotNullColumnEqualFieldAnd(query.getParameter(),Query.PARAMETER_PREFIX);
		String where = query.getNotNullColumnEqualFieldAndVia(handler);
		where = makeCondition(where, query);
		if (where != null && where.length() > 0) {
			sb.append(" WHERE ").append(where);
		}

		// 그룹방식, HAVING, 정렬방식 추가 by skoh
		sb.replace(0, sb.length(), makeGroupSql(sb.toString(), query));

		if (query.hasOrderBy())
			sb.append(" ORDER BY ").append(query.buildOrderBy());
		return getBoundSql(sb.toString(),queryParam);
	}

	/**
	 * 필드, 힌트, 테이블 추가
	 * 
	 * @param sql
	 * @param query
	 * @return
	 */
	protected String makeSelectSql(String sql, Query query) {
		if (Utils.isValidate(query.getFields())) {
			sql = Utils.replaceLastString(sql, "SELECT", "FROM", query.getFields());
		}

		if (Utils.isValidate(query.getHint())) {
			sql = Utils.insertString(sql, "SELECT", query.getHint());
		}

		if (Utils.isValidate(query.getTable())) {
			sql = Utils.replaceLastString(sql, "FROM", query.getTable());
			if (query.getTable().startsWith("TABLE ")) {
				sql = StringUtil.replace(sql, "SELECT", "");
				sql = StringUtil.replace(sql, "FROM", "");
			}
		}

		return sql;
	}

	/**
	 * 그룹방식, HAVING 추가
	 * 
	 * @param sql
	 * @param query
	 * @return
	 */
	protected String makeGroupSql(String sql, Query query) {
		StringBuilder sb = new StringBuilder(sql);

		if (Utils.isValidate(query.getGroupBy())) {
			sb.append(" GROUP BY ").append(query.getGroupBy());
		}

		if (Utils.isValidate(query.getHaving())) {
			sb.append(" HAVING ").append(query.getHaving());
		}

		return sb.toString();
	}
}
