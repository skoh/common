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
package org.mybatisorm.annotation.handler;

import org.apache.ibatis.mapping.SqlCommandType;
import org.mybatisorm.annotation.SqlCommand;
import org.mybatisorm.exception.AnnotationNotFoundException;

public class SqlCommandAnnotation {
	public static SqlCommand getSqlCommand(Class<?> sqlSourceClass) {
		SqlCommand cmd = sqlSourceClass.getAnnotation(SqlCommand.class); 
		if (cmd == null)
			throw new AnnotationNotFoundException(sqlSourceClass.getName() + " has no @SqlCommand annotation.");
		return cmd;
	}
	
	public static SqlCommandType getSqlCommandType(Class<?> sqlSourceClass) {
		return getSqlCommand(sqlSourceClass).value();
	}
	
}
