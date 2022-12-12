package phonebook.hashes;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>{@link OrderedLinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with
 * <b>Ordered Linear Probing</b> as its collision resolution strategy: every key collision is resolved by moving
 * one address over, and the keys in the chain is in order. It suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author YOUR NAME HERE!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class OrderedLinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/

    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public OrderedLinearProbingHashTable(boolean soft){
    	this.primeGenerator = new PrimeGenerator();
        this.softFlag = soft;
        this.table = new KVPair[primeGenerator.getCurrPrime()];
        this.count = 0;
    }



    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     *
     * Different from {@link LinearProbingHashTable}, the keys in the chain are <b>in order</b>. As a result, we might increase
     * the cost of insertion and reduce the cost on search miss. One thing to notice is that, in soft deletion, we ignore
     * the tombstone during the reordering of the keys in the chain. We will have some example in the writeup.
     *
     * Instances of {@link OrderedLinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     * @param key The record's key.
     * @param value The record's value.
     * @throws IllegalArgumentException if either argument is {@code null}.
     * @return The {@link phonebook.utils.Probes} with the value added and the number of probes it makes.
     */
    @Override
    public Probes put(String key, String value) {
    	if (key == null || value == null) {
    		throw new IllegalArgumentException();
    	}
    	
        float capacity = (float) count() / this.primeGenerator.getCurrPrime();
        int M = this.primeGenerator.getCurrPrime();
    	int probes = 0;
    	
        if (capacity >= 0.5) {
        	M = this.primeGenerator.getNextPrime();
        	KVPair[] temp = table; 
        	table = new KVPair[M];
        	count = 0;
        	for (KVPair cur: temp) {
        		probes += 1;
        		if (cur != null && !cur.getKey().equals(TOMBSTONE.getKey())) {
        			probes += put(cur.getKey(), cur.getValue()).getProbes();
        		}
        	}
        }   

        int start = hash(key);
    	int ctr = 0;
        KVPair toIns = new KVPair(key, value);
        probes += 1;
        while (table[(start + ctr) % M] != null && ctr < M) {
        	probes += 1;
        	
        	if (table[(start + ctr) % M].getKey().compareTo(toIns.getKey()) > 0) {
        		KVPair temp = table[(start + ctr) % M];
        		table[(start + ctr) % M] = toIns;
        		toIns = temp;
        	}
			ctr += 1;
		}
        
        count += 1;
        if (ctr < M) {
        	table[(start + ctr) % M] = toIns;
        	return new Probes(key, probes);
        } else {
        	return null;
        }
    }

    @Override
    public Probes get(String key) {
    	// Look at current spot
    	int start = hash(key);
    	int ctr = 0;
    	int M = this.primeGenerator.getCurrPrime();
    	
    	// Loop over all i's or till we find it -> could use while
		while (table[(start + ctr) % M] != null && 
			   !table[(start + ctr) % M].getKey().equals(key) 
			   && ctr < M
			   && table[(start + ctr) % M].getKey().compareTo(key) < 0) {
			
			ctr += 1;
		}
		
		if (table[(start + ctr) % M] != null && table[(start + ctr) % M].getKey().equals(key)) {
			return new Probes(table[(start + ctr) % M].getValue(), ctr + 1);
		} else {
			return new Probes(null, ctr + 1);
		}
    }


    /**
     * <b>Return</b> the value associated with key in the {@link HashTable}, and <b>remove</b> the {@link phonebook.utils.KVPair} from the table.
     * If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key doesn't exist in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
    	if (key == null) 
    		return new Probes(null, 1);
    	
    	if (get(key).getValue() == null)
    		return get(key);
    	
    	// Look at current spot
    	int start = hash(key);
    	int ctr = 0, probes = 0;
    	int M = this.primeGenerator.getCurrPrime();
    	
    	// Loop over all i's or till we find it -> could use while
    	probes += 1;
		while (table[(start + ctr) % M] != null && !table[(start + ctr) % M].getKey().equals(key)) {
			probes += 1;
			ctr += 1;
		}
		
		String value = table[(start + ctr) % M].getValue();
		table[(start + ctr) % M] = (this.softFlag) ? TOMBSTONE : null;
		
		if(!this.softFlag) {
			ctr += 1;
			probes += 1;
			while (table[(start + ctr) % M] != null) {
				KVPair cur = table[(start + ctr) % M];
				table[(start + ctr) % M] = null;
				count--;
				probes += put(cur.getKey(), cur.getValue()).getProbes();
				probes += 1;
				ctr += 1;
			}
		}
		
		count -= 1;
		return new Probes(value, probes);
    }

    @Override
    public boolean containsKey(String key) {
    	for (KVPair cur: table) {
        	if (cur.getKey().equals(key)) {
        		return true;
        	}
        }
        
        return false; // ERASE THIS LINE AFTER IMPLEMENTING THIS METHOD!
    }

    @Override
    public boolean containsValue(String value) {
    	for (KVPair cur: table) {
        	if (cur.getValue().equals(value)) {
        		return true;
        	}
        }
        
        return false;
    }

    @Override
    public int size() {
    	return count;
    }

    @Override
    public int capacity() {
    	return table.length;
    }

}
