/**
 * 
 */
package prototype.bloomer;

/**
 * @author mark.davis
 * Experimental Bloom Filter implementation for determination of existence in an intersected set of two values
 * Uses reference counting to allow for deletions. 
 * Attempts to optimize size and can trigger rebuild as necessary.
 *  
 */
public class IntersectionFilter {
	
	protected byte[] buckets;
	protected int numBuckets = 0;
	protected int numEntries = 0;
	protected short numHashes = 3;
	protected Hasher[] hashes;
	
	/**
	 * Primary constructor. Creates buckets sizes based on input parameters
	 * 
	 * @param maxX Maximum number of elements on X axis. Must be greater than 0.
	 * @param maxY Maximum number of elements on X axis. Must be greater than 0.
	 * @param targetUsagePercent Percentage of expected permutations to store as a value 1-99.
	 * @throws Exception On any unhandled issues bubbled up
	 */
	public IntersectionFilter( int maxX, int maxY, int targetUsagePercent ) throws Exception {
		numBuckets = calcSize( maxX, maxY, targetUsagePercent );
		buckets = new byte[numBuckets];
		
		// create the hashers
		hashes = new Hasher[numHashes];
		for( short h=0; h<numHashes; h++ ) {
			Hasher newHasher = new Hasher( h );
			hashes[h] = newHasher;			
		}
	}

	/**
	 * @param valX
	 * @param valY
	 * @return
	 * @throws Exception
	 */
	public boolean Add( String valX, String valY ) throws Exception {
		if( ! ValidateXY( valX, valY ) ) return false;
		
		// Hashkey is concatenation of two input strings with a + char
		// TODO: string trimming
		String hashStr = valX + "+" + valY;
		
		// loop through each hash and increment the appropriate bucket
		for( int h=0; h<numHashes; h++ ) {
			long rawHash = hashes[h].getHash( hashStr );
			int hashVal = (int) Math.abs( rawHash % buckets.length );
			if( buckets[hashVal]++ < 0 ) { // overflow
				throw new Exception( "Bucket overflow: bucket #" + hashVal );
			}
		}
		
		numEntries++;
		return true;
	}
	
	public boolean Delete( String valX, String valY ) throws Exception {
		if( ! ValidateXY( valX, valY ) ) return false;

		// Hashkey is concatenation of two input strings with a + char
		// TODO: string trimming
		String hashStr = valX + "+" + valY;
		
		// TODO: optimize the existence check with the bucket deletion to remove redundant hashing
		if( ! this.Exists( valX, valY ) ) return false;
		
		// loop through each hash and decrement the appropriate bucket
		for( int h=0; h<numHashes; h++ ) {
			long rawHash = hashes[h].getHash( hashStr );
			int hashVal = (int) Math.abs( rawHash % buckets.length );
			buckets[hashVal]++;
		}
		
		numEntries--;
		return true;
	}
	
	public boolean Exists(String valX, String valY ) throws Exception {
		if( ! ValidateXY( valX, valY ) ) return false;
		
		// Hashkey is concatenation of two input strings with a + char
		// TODO: string trimming
		String hashStr = valX + "+" + valY;
		
		// loop through each hash and check the appropriate bucket
		// return false at the first occurrence of an empty bucket
		for( int h=0; h<numHashes; h++ ) {
			long rawHash = hashes[h].getHash( hashStr );
			int hashVal = (int) Math.abs( rawHash % buckets.length );
			if( buckets[hashVal] < 1 ) return false;
		}
		
		return true;
	}
	
	public int Count() {
		return numEntries;
	}
	
	/*
	 * Resets the storage on the filter to zero, eliminating all entries.
	 * @result Result of the operation success
	 */
	public boolean Reset() throws Exception {
		for( byte b : buckets ) {
			b = 0;
		}
		numEntries = 0;
		return true;
	}

	protected boolean ValidateXY( String valX, String valY ) {
		if( null == valX )
			return false;
		else if ( valX.isEmpty() )
			return false;
		
		if( null == valY || valY.isEmpty() )
			return false;
		
		return true;
	}
	
	protected int calcSize( int x, int y, int usage ) throws Exception {
		if( usage < 0 || usage > 100 )
			throw new Exception( "calcSize called with usage outside of 0-100: " + usage );
		
		int maxSize = x * y * usage / 100;
		maxSize *= kFudgeFactor;
		
		return maxSize;
	}
	
	protected final float kFudgeFactor = (float) 1.5;
	
}
