/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.core.parsing;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.shardingjdbc.core.api.fixture.ShardingRuleMockBuilder;
import io.shardingjdbc.core.constant.DatabaseType;
import io.shardingjdbc.core.parsing.parser.exception.SQLParsingUnsupportedException;
import io.shardingjdbc.test.sql.SQLCase;
import io.shardingjdbc.test.sql.SQLCasesLoader;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@AllArgsConstructor
@RunWith(Parameterized.class)
public final class ParsingEngineForUnsupportedSQLTest {
    
    private String testCaseName;
    
    private String sql;
    
    private DatabaseType dbType;
    
    @Parameters(name = "{0}In{2}")
    public static Collection<Object[]> getTestParameters() {
        Collection<Object[]> result = new ArrayList<>();
        for (SQLCase each : SQLCasesLoader.getInstance().getUnsupportedSQLCaseMap().values()) {
            result.addAll(getTestParameters(each));
        }
        return result;
    }
    
    private static Collection<Object[]> getTestParameters(final SQLCase sqlCase) {
        Collection<Object[]> result = new LinkedList<>();
        for (DatabaseType each : getDatabaseTypes(sqlCase.getDatabaseTypes())) {
            Object[] parameters = new Object[3];
            parameters[0] = sqlCase.getId();
            parameters[1] = sqlCase.getValue();
            parameters[2] = each;
            result.add(parameters);
        }
        return result;
    }
    
    private static Set<DatabaseType> getDatabaseTypes(final String databaseTypes) {
        if (Strings.isNullOrEmpty(databaseTypes)) {
            return Sets.newHashSet(DatabaseType.values());
        }
        Set<DatabaseType> result = new HashSet<>();
        for (String each : databaseTypes.split(",")) {
            result.add(DatabaseType.valueOf(each));
        }
        return result;
    }
    
    @Test(expected = SQLParsingUnsupportedException.class)
    public void assertUnsupportedSQL() {
        new SQLParsingEngine(dbType, sql, new ShardingRuleMockBuilder().addShardingColumns("user_id").addShardingColumns("order_id").addGenerateKeyColumn("t_order", "order_id").build()).parse();
    }
}
