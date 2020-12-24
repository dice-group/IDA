package org.dice.ida;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.util.DbUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
public class DbUtilsTests {
	@Test
	public void testmanageNullValues() {
		assertEquals(DbUtils.manageNullValues(null), IDAConst.NULL_VALUE_IDENTIFIER);
	}

	@Test
	public void negTestmanageNullValues() {
		assertNotEquals(DbUtils.manageNullValues("test"), IDAConst.NULL_VALUE_IDENTIFIER);
	}
}
