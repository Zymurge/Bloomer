package prototype.bloomer;

public class Hasher {
	
	protected short myAlgo = -1;
	
	//TODO: beef up cache to n entries
	protected String lastTarget;
	protected long lastResult;
	
	public Hasher( short algo ) {
		myAlgo = algo;
	}

	public long getHash( String target ) throws Exception {
		// Check for cache hit
		if( target.equals( lastTarget ) ) {
			return lastResult;
		}

		long result = getHash( target, myAlgo );
		// push onto cache
		lastTarget = target;
		lastResult = result;
		
		return result;
	}

	private long getHash( String target, int hashNum ) throws Exception {
		switch ( hashNum ) {
			case 0: return hash0( target );
			case 1: return hash1( target );
			case 2: return hash2( target );
			case 3: return hash3( target );
			default: throw new Exception( "Unknown hashNum algorithm: " + hashNum );
		}
	}
	
	private long hash0( String target ) {
		long hash = 5381;
		byte[] chars = target.getBytes();
		for( byte b : chars ) {
			hash = ((hash << 5) + hash) + b;
		}
		return hash;
	}

	private long hash1( String target ) {
		long hash = 0;
		byte[] chars = target.getBytes();
		for( byte b : chars ) {
			hash = b + (hash << 6) + (hash << 16) - hash;
		}
		return hash;
	}
	
	private long hash2( String target ) {
        long sum1 = 0xffff, sum2 = 0xffff;
		byte[] chars = target.getBytes();
		for( byte b : chars ) {
			sum1 += b;
            sum2 += sum1;
        } 
		sum1 = (sum1 & 0xffff) + (sum1 >> 16);
        sum2 = (sum2 & 0xffff) + (sum2 >> 16);

        /* Second reduction step to reduce sums to 16 bits */
        sum1 = (sum1 & 0xffff) + (sum1 >> 16);
        sum2 = (sum2 & 0xffff) + (sum2 >> 16);
        return sum2 << 16 | sum1;
	}

	private long hash3( String target ) {
		return 0;
	}
}
