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
package org.mybatisorm.sql.source.sqlserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.mybatisorm.Query;
import org.mybatisorm.annotation.handler.FieldList;
import org.mybatisorm.sql.source.AbstractInsertSqlSource;

public class InsertSqlSource extends AbstractInsertSqlSource {

	private static Log logger = LogFactory.getLog(InsertSqlSource.class);
	
	public InsertSqlSource(SqlSourceBuilder sqlSourceParser, Class<?> clazz) {
		super(sqlSourceParser,clazz);
	}

	public BoundSql getBoundSql(final Object parameter) {
		// parameter 구분 by skoh
//		return getBoundSql(parameter,handler.getNotNullFieldList(parameter));
		FieldList fieldList = null;
		if (parameter instanceof Query) {
			Query query = (Query) parameter;
			fieldList = handler.getNotNullFieldList(query.getParameter());
		} else {
			fieldList = handler.getNotNullFieldList(parameter);
		}
		return getBoundSql(parameter, fieldList);
	}
}
