package org.dice.ida;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.util.DbUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.assertEquals;

@SpringBootTest
public class DbUtilsTests {
	@Test
	public void testmanageNullValues() {
		System.out.println("*********************Test case*****************");
		assertEquals(DbUtils.manageNullValues(null), IDAConst.NULL_VALUE_IDENTIFIER);
		System.out.println("*********************Test case done*****************");
	}
}
