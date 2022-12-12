package bpt;

import bpt.UnimplementedMethodException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * {@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;
 * 0, 1 &#125;. By restricting themselves to this small but terrifically useful
 * alphabet, Binary Patricia Tries combine all the positive aspects of Patricia
 * Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.
 * </p>
 *
 * @author Joe Barresi
 */
public class BinaryPatriciaTrie {

	/*
	 * We are giving you this class as an example of what your inner node might look
	 * like. If you would prefer to use a size-2 array or hold other things in your
	 * nodes, please feel free to do so. We can *guarantee* that a *correct*
	 * implementation exists with *exactly* this data stored in the nodes.
	 */
	static class TrieNode {
		TrieNode left, right;
		String str;
		boolean isKey;

		// Default constructor for your inner nodes.
		TrieNode() {
			this("", false);
		}

		// Non-default constructor.
		TrieNode(String str, boolean isKey) {
			left = right = null;
			this.str = str;
			this.isKey = isKey;
		}
	}

	private TrieNode root;
	private int count;
	

	/**
	 * Simple constructor that will initialize the internals of {@code this}.
	 */
	public BinaryPatriciaTrie() {
		root = new TrieNode();
		count = 0;
	}

	/**
	 * Searches the trie for a given key.
	 *
	 * @param key The input {@link String} key.
	 * @return {@code true} if and only if key is in the trie, {@code false}
	 *         otherwise.
	 */
	public boolean search(String key) {
		if (count == 0) {
			return false;
		}

		String res = key;
		TrieNode cur = root;
		
		// Assume we are properly searching.
		while (res.length() > 0) {
			cur = (res.charAt(0) == '0') ? cur.left : cur.right;
			if (cur == null || res.length() < cur.str.length() || 
					!cur.str.equals(res.substring(0, cur.str.length()))) {
				return false;
			}
			if (cur.str.length() < res.length()) {
				res = res.substring(cur.str.length());
			} else {
				res = "";
			}

		}
		return cur.isKey;
	}

	/**
	 * Inserts key into the trie.
	 *
	 * @param key The input {@link String} key.
	 * @return {@code true} if and only if the key was not already in the trie,
	 *         {@code false} otherwise.
	 */
	public boolean insert(String key) {
		String res = key;
		TrieNode cur = root, par = root;
		boolean isLeft = false, retVal = !search(key);
		// Assume we are properly searching.
		if (retVal) {
			count++;
			while (res.length() > 0) {
				par = cur;
				cur = (res.charAt(0) == '0') ? cur.left : cur.right;
				isLeft = (res.charAt(0) == '0') ? true : false;
				if (cur == null || res.length() < cur.str.length()
						|| !cur.str.equals(res.substring(0, cur.str.length())))
					break;

				res = res.substring(cur.str.length());
			}

			if (cur == null) {
				if (isLeft) {
					par.left = new TrieNode(res, true);
				} else {
					par.right = new TrieNode(res, true);
				}
			} else if(res.equals("")) {
				cur.isKey = true;
			} else if (cur.str.contains(res)) {
				String rem = cur.str.substring(res.length());
				cur.str = rem;
				if (isLeft) {
					par.left = new TrieNode(res, true);
					if (rem.charAt(0) == '0') {
						par.left.left = cur;
					} else {
						par.left.right = cur;
					}
				} else {
					par.right = new TrieNode(res, true);
					if (rem.charAt(0) == '0') {
						par.right.left = cur;
					} else {
						par.right.right = cur;
					}
				}
			} else {
				String common = "";
				int i = 0;
				while (res.charAt(i) == cur.str.charAt(i)) {
					common += res.charAt(i);
					i += 1;
				}

				String exist = cur.str.substring(common.length());
				cur.str = exist;
				String newStr = res.substring(common.length());
				TrieNode split;
				if (isLeft) {
					par.left = new TrieNode(common, false);
					split = par.left;
				} else {
					par.right = new TrieNode(common, false);
					split = par.right;
				}
			
				if (exist.charAt(0) == '0') {
					split.left = cur;
					split.right = new TrieNode(newStr, true);
				} else {
					split.right = cur;
					split.left = new TrieNode(newStr, true);
				}
			}
		}
		return retVal;
	}

	/**
	 * Deletes key from the trie.
	 *
	 * @param key The {@link String} key to be deleted.
	 * @return {@code true} if and only if key was contained by the trie before we
	 *         attempted deletion, {@code false} otherwise.
	 */
	public boolean delete(String key) {
		String res = key;
		TrieNode cur = root, par = root;
		boolean isLeft = false, retVal = search(key);

		if (retVal) {
			count--;
			
			// We know this node can be found
			while (res.length() > 0) {
				par = cur;
				cur = (res.charAt(0) == '0') ? cur.left : cur.right;
				isLeft = (res.charAt(0) == '0') ? true : false;
				res = res.substring(cur.str.length());
			}
			
			if (cur.left != null && cur.right != null) {
				cur.isKey = false;
			} else if (cur.left != null) {
				if (isLeft) {
					String newStr = cur.str + cur.left.str;
					cur.left.str = newStr;
					par.left = cur.left;
				} else {
					String newStr = cur.str + cur.left.str;
					cur.left.str = newStr;
					par.right = cur.left;
				}
			} else if (cur.right != null) {
				if (isLeft) {
					String newStr = cur.str + cur.right.str;
					cur.right.str = newStr;
					par.left = cur.right;
				} else {
					String newStr = cur.str + cur.right.str;
					cur.right.str = newStr;
					par.right = cur.right;
				}
			} else {
				if (isLeft) {
					par.left = null;
				} else {
					par.right = null;
				}
			}
		}
			
		return retVal;
	}

	/**
	 * Queries the trie for emptiness.
	 *
	 * @return {@code true} if and only if {@link #getSize()} == 0, {@code false}
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Returns the number of keys in the tree.
	 *
	 * @return The number of keys in the tree.
	 */
	public int getSize() {
		return count;
	}

	/**
	 * <p>
	 * Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie.
	 * Remember from lecture that inorder traversal in tries is NOT sorted
	 * traversal, unless all the stored keys have the same length. This is of course
	 * not required by your implementation, so you should make sure that in your
	 * tests you are not expecting this method to return keys in lexicographic
	 * order. We put this method in the interface because it helps us test your
	 * submission thoroughly and it helps you debug your code!
	 * </p>
	 *
	 * <p>
	 * We <b>neither require nor test </b> whether the {@link Iterator} returned by
	 * this method is fail-safe or fail-fast. This means that you do <b>not</b> need
	 * to test for thrown {@link java.util.ConcurrentModificationException}s and we
	 * do <b>not</b> test your code for the possible occurrence of concurrent
	 * modifications.
	 * </p>
	 *
	 * <p>
	 * We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do
	 * <b>not</b> test for the behavior of {@link Iterator#remove()}. You can handle
	 * it any way you want for your own application, yet <b>we</b> will <b>not</b>
	 * test for it.
	 * </p>
	 *
	 * @return An {@link Iterator} over the {@link String} keys stored in the trie,
	 *         exposing the elements in <i>symmetric order</i>.
	 */
	public Iterator<String> inorderTraversal() {
		return new BPTIterator(this);
	}

	class BPTIterator<String> implements Iterator<String> {
		ArrayList<String> words;
		int ptr;

		public BPTIterator(BinaryPatriciaTrie cur) {
			words = new ArrayList<String>();
			populate(cur.root, (String) "");
			ptr = 0;
		}
		
		private void populate(TrieNode cur, String tail) {
			if (cur.left != null) {
				populate(cur.left, (String) (tail + cur.str));
			}
			
			if (cur.isKey) {
				words.add((String) (tail + cur.str));
			}
			
			if (cur.right != null) {
				populate(cur.right, (String) (tail + cur.str));
			}
		}
		
		@Override
		public boolean hasNext() {
			return ptr < words.size();
		}

		@Override
		public String next() {
			return words.get(ptr++);
		}
		
	}
	/**
	 * Finds the longest {@link String} stored in the Binary Patricia Trie.
	 * 
	 * @return
	 *         <p>
	 *         The longest {@link String} stored in this. If the trie is empty, the
	 *         empty string &quot;&quot; should be returned. Careful: the empty
	 *         string &quot;&quot;is <b>not</b> the same string as &quot; &quot;;
	 *         the latter is a string consisting of a single <b>space character</b>!
	 *         It is also <b>not the same as the</b> null <b>reference</b>!
	 *         </p>
	 *
	 *         <p>
	 *         Ties should be broken in terms of <b>value</b> of the bit string. For
	 *         example, if our trie contained only the binary strings 01 and 11,
	 *         <b>11</b> would be the longest string. If our trie contained only 001
	 *         and 010, <b>010</b> would be the longest string.
	 *         </p>
	 */
	public String getLongest() {
		Iterator<String> obj = this.inorderTraversal();
		String longest = "";
		while(obj.hasNext()) {
			String cur = obj.next();

			if (cur.length() > longest.length() || 
				(cur.length() == longest.length() && Integer.parseInt(cur, 2) > Integer.parseInt(longest, 2))) {
				longest = cur;
			}
		}
		
		return longest;
	}

	/**
	 * Makes sure that your trie doesn't have splitter nodes with a single child. In
	 * a Patricia trie, those nodes should be pruned.
	 * 
	 * @return {@code true} iff all nodes in the trie either denote stored strings
	 *         or split into two subtrees, {@code false} otherwise.
	 */
	public boolean isJunkFree() {
		return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
	}

	private boolean isJunkFree(TrieNode n) {
		if (n == null) { // Null subtrees trivially junk-free
			return true;
		}
		if (!n.isKey) { // Non-key nodes need to be strict splitter nodes
			return ((n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right));
		} else {
			return (isJunkFree(n.left) && isJunkFree(n.right)); // But key-containing nodes need not.
		}
	}
}
