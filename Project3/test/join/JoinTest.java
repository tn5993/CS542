package join;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import relation.Record;
import relation.Relation;
import condition.CompareType;
import exception.DirtyDataException;

public class JoinTest {
	Relation relation1;
	Relation relation2;
	Join joinCond;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
        
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testJoinOnEqualityCond() throws DirtyDataException, FileNotFoundException {
		relation1 = newRelation("city", "testresource/scenerio1/city.csv");
		relation2 = newRelation("country", "testresource/scenerio1/country.csv");
		relation1.open();
		relation2.open();
		joinCond = new Join(relation1, relation2);
        joinCond.addCondition("CountryCode", "Id", CompareType.EQUAL);
        
		Collection<Record> records = doJoin(joinCond, null);
		assertEquals(2, records.size()); //only two result can join which is 10 and 100
	}
	
	@Test
	public void testJoinOnPercentageCond() throws DirtyDataException, FileNotFoundException {
		relation1 = newRelation("country", "country.csv");
		relation2 = newRelation("city", "city.csv");
		relation1.open();
		relation2.open();
		joinCond = new Join(relation1, relation2);
		joinCond.addCondition("Population", "Population", CompareType.INTEGER_SMALLER, 2.5);
		Collection<Record> records = doJoin(joinCond, null);
		
		double FourtyPercentOfCountry = 240 * 0.4;
		assertTrue((double) records.size() > FourtyPercentOfCountry);
	}
	
	
	@Test
	public void testProjection() throws FileNotFoundException, DirtyDataException {
		relation1 = newRelation("city", "testresource/scenerio1/city.csv");
		relation2 = newRelation("country", "testresource/scenerio1/country.csv");
		relation1.open();
		relation2.open();
		joinCond = new Join(relation1, relation2);
        joinCond.addCondition("CountryCode", "Id", CompareType.EQUAL);
        Collection<Record> records = doJoin(joinCond, new String[] { "City" }); //projection: city
        assertEquals(2, records.size());
        for (Record r : records) {
        	assertEquals(1, r.getValues().length); //only one attribute selected, which is city
        }
	}
	
	public Relation newRelation(String name, String path) throws FileNotFoundException {
		return new Relation(name, path);
	}
	
	public Collection<Record> doJoin(Join j, String[] projection) {
		Collection<Record> allRecords = new ArrayList<Record>();
		try {
			Record record = null;
			while ((record = j.getNext()) != null) {
				if (projection != null) {
					record.select(projection);
				}
            	allRecords.add(record);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allRecords;
	}
	

}
