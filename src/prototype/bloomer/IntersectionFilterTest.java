/**
 * 
 */
package prototype.bloomer;

import junit.framework.TestCase;

/**
 * @author mark.davis
 *
 */
public class IntersectionFilterTest extends TestCase {

	protected IntersectionFilter testFilter;
	private final int kSizeX = 500;
	private final int kSizeY = 300;
	private final int kUsage = 25;

	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testFilter = new IntersectionFilter( kSizeX, kSizeY, kUsage );
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#IntersectionFilter(int, int, int)}.
	 */
	public void testIntersectionFilter() {
		assertNotNull( "setUp failed to instantiate testFilter", testFilter );
		assertEquals( "Count at instantiation should be zero", 0, testFilter.Count() );
	}
	
	public void testConstructorParamValidation() {
		String errMsg = null;
		try {
			new IntersectionFilter( -1, 1, 50 );
		}
		catch( Exception e ) {
			errMsg = e.getMessage();
		}
		assertTrue( "Expected exception not thrown", errMsg != null );
		assertTrue( "Didn't find expected exception message", errMsg.contains( "illegal parameter" ) );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Add(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testAddByReturnVal() throws Exception {
		boolean result = tryAdd( "valX_add1", "valY_add1" );
		assertTrue( "Expected positive response from Add operation", result );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Add(java.lang.String, java.lang.String)}.
	 */
	public void testAddByCount() {
		int expectedCount = testFilter.Count() + 1;
		boolean result = tryAdd( "valX_test2", "valY_test2" );
		if( ! result ) {
			fail( "Could not add element for some reason" );
		}
		assertSame( "Add method should increment count by 1", expectedCount, testFilter.Count() );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Add(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testAddNullValues() throws Exception {
		assertFalse( "null X arg should yield false", testFilter.Add( null, "valY" ) );
		assertFalse( "null Y arg should yield false", testFilter.Add( "valX", null ) );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Delete(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testDeleteByReturnVal() throws Exception {
		if( ! tryAdd( "valX_del1", "valY_del1" ) )
			fail( "Could not set initial element to delete" );
		
		boolean result = tryDelete( "valX_del1", "valY_del1" );
		assertTrue( "Expected positive response from Delete operation", result );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Delete(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testDeleteByCount() throws Exception {
		int expectedCount = testFilter.Count();
		if( ! tryAdd( "valX_del2", "valY_del2" ) ) {
			fail( "Could not set initial element to delete" );
		}

		if( ! tryDelete( "valX_del2", "valY_del2" ) ) {
			fail( "Could delete target element" );
		}
		assertSame( "Delete method should decrement count by 1", expectedCount, testFilter.Count() );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Exists(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testExistsPositive() throws Exception {
		if( ! tryAdd( "valX_exi1", "valY_exi1" ) ) {
			fail( "Could not add initial element to check for existence" );
		}
		
		assertTrue( testFilter.Exists( "valX_exi1", "valY_exi1") );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Exists(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	public void testExistsNegtive() throws Exception {
		assertFalse( testFilter.Exists( "Non-exist", "Non-exist") );
	}

	/**
	 * Test method for {@link prototype.bloomer.IntersectionFilter#Count()}.
	 */
	public void testCount() {
		try {
			if( ! testFilter.Reset() )
				fail( "Could not reset the filter for zeroing count" );
		} catch ( Exception e ) {
			fail( "Exception thrown from Reset method" );
		}
		
		if( ! ( tryAdd( "X-cnt1", "Y-cnt1" ) &&
				tryAdd( "X-cnt1", "Y-cnt2" ) &&
				tryAdd( "X-cnt2", "Y-cnt1" ) &&
				tryAdd( "X-cnt2", "Y-cnt2" ) )
			)
			fail( "Could not add all entries for count test" );
		
		assertSame( "Count does not match added results", 4, testFilter.Count() );
	}
	
	public void testCountLarge() {
		int xSize = kSizeX * kUsage / 100;
		for( int x=0; x<=xSize; x++ ) {
			String xStr = "foo_" + x;
			for( int y=0; y<kSizeY; y++ ) {
				String yStr = "barr_" + y;
				tryAdd( xStr, yStr );
			}
		}
				
		assertTrue( "Count should be big!", testFilter.Count() >= ( xSize*kSizeY ) );
		boolean fpCheck = false;
		try {
			fpCheck = 
				testFilter.Exists( "abcdef", "123xyz" ) ||
				testFilter.Exists( "abcdef", "98$" ) ||
				testFilter.Exists( "fred_flintstone", "xxx" ) ||
				testFilter.Exists( "00F45Ac3ee", "12bBaa6A95" )
				;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail( "Exception tossed by Exists call" );
		}
		assertFalse( "This is a false positive", fpCheck );
	}

	protected boolean tryAdd( String inX, String inY ) {
		boolean result = false;
		try {
			result = testFilter.Add( inX, inY );
		}
		catch( Exception e ) {
			fail( "Add operation threw exception" );
		}
		return result;
	}

	protected boolean tryDelete( String inX, String inY ) {
		boolean result = false;
		try {
			result = testFilter.Delete( inX, inY );
		}
		catch( Exception e ) {
			fail( "Delete operation threw exception" );
		}
		return result;
	}

}
