package phonebook.hashes;

import phonebook.exceptions.UnimplementedMethodException;
import phonebook.utils.KVPair;
import phonebook.utils.PrimeGenerator;
import phonebook.utils.Probes;

/**
 * <p>
 * {@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable}
 * which uses <b>Quadratic Probing</b> as its collision resolution strategy.
 * Quadratic Probing differs from <b>Linear</b> Probing in that collisions are
 * resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key
 * insertion which generates several collisions, the first collision will be
 * resolved by moving 1^2 + 1 = 2 positions over from the originally hashed
 * address (like Linear Probing), the second one will be resolved by moving 2^2
 * + 2= 6 positions over from our hashed address, the third one by moving 3^2 +
 * 3 = 12 positions over, etc.
 * </p>
 *
 * <p>
 * By using this collision resolution technique,
 * {@link QuadraticProbingHashTable} aims to get rid of the &quot;key clustering
 * &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many
 * collisions. The tradeoff is that, in doing so,
 * {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.
 * </p>
 *
 * @author YOUR NAME HERE!
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

	/* ********************************************************************/
	/* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
	/* ********************************************************************/
	
	private int qp(int i) {
		return (i - 1) + ((i - 1) * (i - 1));
	}

	/* ******************************************/
	/* IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
	/* **************************************** */

	/**
	 * Constructor with soft deletion option. Initializes the internal storage with
	 * a size equal to the starting value of {@link PrimeGenerator}.
	 * 
	 * @param soft A boolean indicator of whether we want to use soft deletion or
	 *             not. {@code true} if and only if we want soft deletion,
	 *             {@code false} otherwise.
	 */
	public QuadraticProbingHashTable(boolean soft) {
		this.primeGenerator = new PrimeGenerator();
		this.softFlag = soft;
		this.table = new KVPair[primeGenerator.getCurrPrime()];
		this.count = 0;
	}

	@Override
	public Probes put(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException();
		}

		float capacity = (float) count() / this.primeGenerator.getCurrPrime();
		int M = this.primeGenerator.getCurrPrime();
		int probes = 0;

		if (capacity > 0.5) {
			M = this.primeGenerator.getNextPrime();

			KVPair[] temp = table;
			table = new KVPair[M];
			count = 0;
			for (KVPair cur : temp) {
				probes += 1;
				if (cur != null && !cur.getKey().equals(TOMBSTONE.getKey())) {
					probes += put(cur.getKey(), cur.getValue()).getProbes();
				}
			}
		}

		int start = hash(key), i = 1;

		probes += 1;
		while (table[(start + qp(i)) % M] != null) {
			i += 1;
			probes += 1;
		}

		table[(start + qp(i)) % M] = new KVPair(key, value);
		count += 1;
		return new Probes(value, probes);
	}

	@Override
	public Probes get(String key) {
		if (count == 0) {
			return new Probes(null, 1);
		}

		int M = this.primeGenerator.getCurrPrime();
		int start = hash(key), i = 1, probes = 0;

		probes += 1;
		while (table[(start + qp(i)) % M] != null
				&& !table[(start + qp(i)) % M].getKey().equals(key)) {
			i += 1;
			probes += 1;
		}

		KVPair found = table[(start + qp(i)) % M];
		if (found != null && found.getKey().equals(key)) {
			return new Probes(found.getValue(), probes);
		} else {
			return new Probes(null, probes);
		}
	}

	@Override
	public Probes remove(String key) {
		// Throw out invalid keys
		if (get(key) == null || get(key).getValue() == null) {
			return new Probes(null, 1);
		}

		// get current modulus, initial hash, initialize i and probes
		int M = this.primeGenerator.getCurrPrime();
		int start = hash(key), i = 1, probes = 0;

		//Initlialize Probe accounts for off bhy one error
		probes += 1;
		while (table[(start + qp(i)) % M] != null
				&& !table[(start + qp(i)) % M].getKey().equals(key)) {
			i += 1;
			probes += 1;
		}

		// Get current value of the johnny
		String val = table[(start + qp(i)) % M].getValue();
		// set value at current spot
		table[(start + qp(i)) % M] = (this.softFlag) ? TOMBSTONE : null;

		if (!this.softFlag) {
			// Store table in a seperate johnny
			KVPair[] temp = this.table;
			// Create a new table
			table = new KVPair[M];
			for (KVPair cur : temp) {
				probes += 1;
				if (cur != null && cur != TOMBSTONE) {
					probes += this.put(cur.getKey(), cur.getKey()).getProbes();
					count -= 1;
				}
			}
		}

		count -= 1;
		return new Probes(val, probes);
	}

	@Override
	public boolean containsKey(String key) {
		for (KVPair cur : table) {
			if (cur != null && cur.getKey().equals(key)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsValue(String value) {
		for (KVPair cur : table) {
			if (cur != null && cur.getValue().equals(value)) {
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