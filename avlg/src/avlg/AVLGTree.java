package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p> 
 *
 * @author YOUR NAME HERE!
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private int g;
	private int size;
	private Node root;
	
	private class Node {
		Node left;
		Node right;
		T key;
		
		public int getHeight() {
			if (right == null && left == null) {
				return 0;
			} else if (right == null) {
				return left.getHeight() + 1;
			} else if (left == null) {
				return right.getHeight() + 1;
			} else {
				return Math.max(left.getHeight(), right.getHeight()) + 1;
			}
		}

		public Node(AVLGTree<T>.Node left, AVLGTree<T>.Node right, T key) {
			this.left = left;
			this.right = right;
			this.key = key;
		}

		public T getKey() {
			return key;
		}		
	}
	
	private Node rotateRight(Node tgt) {
		Node temp = tgt.left;
		tgt.left  = temp.right;
		temp.right = tgt;
		return temp;
	}
	
	private Node rotateLeft(Node tgt) {
		Node temp = tgt.right;
		tgt.right  = temp.left;
		temp.left = tgt;
		return temp;
	}
	
	private Node rotateLeftRight(Node tgt) {
		tgt.left = rotateLeft(tgt.left);
		return rotateRight(tgt);
	}
	
    private Node rotateRightLeft(Node tgt) {
    	tgt.right = rotateRight(tgt.right);
		return rotateLeft(tgt);
	}
    
    private int height(Node obj) {
    	if (obj == null) {
    		return -1;
    	}
    	
    	return obj.getHeight();
    }
    
    private Node insertAux(T key, Node cur) {
    	if (cur == null) {
    		return new Node(null, null, key);
    	} 
    	
    	if (key.compareTo(cur.key) < 0) {
    		cur.left = insertAux(key, cur.left);
    		if (height(cur.left) - height(cur.right) > g) {
    			if (key.compareTo(cur.left.getKey()) < 0) {
    				cur = rotateRight(cur);
    			} else {
    				cur = rotateLeftRight(cur);
    			}
    		}
    	} else {
    		cur.right = insertAux(key, cur.right);
    		if (height(cur.right) - height(cur.left) > g) {
    			if (key.compareTo(cur.right.getKey()) > 0) {
    				cur = rotateLeft(cur);
    			} else {
    				cur = rotateRightLeft(cur);
    			}
    		}
    	}
    	
    	return cur;
    }
    
    private int imbal(Node cur) {
    	return height(cur.left) - height(cur.right);
    }
    
    private Node foundAux(Node cur, T key) {
    	if (cur == null) {
    		return null;
    	} else if (cur.key.compareTo(key) == 0) {
    		return cur;
    	} else if (cur.key.compareTo(key) < 0){
    		return foundAux(cur.right, key);
    	} else {
    		return foundAux(cur.left, key);
    	}	
    }
    
    private int maxImbalAux(Node cur) {
    	int left = 0, right = 0;
    	if (cur.left != null) {
    		left = maxImbalAux(cur.left);
    	}
    	
    	if (cur.right != null) {
    		right = maxImbalAux(cur.right);
    	}
    	
    	return Math.max(Math.max(left, right), imbal(cur));
    }
    
    private boolean isBSTA(Node aux) {
    	if (aux.right == null && aux.left == null) {
			return true;
		} else if (aux.right == null) {
			return aux.left.key.compareTo(aux.key) < 0 && isBSTA(aux.left);
		} else if (aux.left == null) {
			return aux.right.key.compareTo(aux.key) > 0 && isBSTA(aux.right);
		} else {
			return aux.left.key.compareTo(aux.key) < 0 && aux.right.key.compareTo(aux.key) > 0
				   && isBSTA(aux.right) && isBSTA(aux.left);
		}
    }
    
 // Due to its implementation in delete, this method is guranteed to have a 
    // key that is in the tree
    private Node deleteAux(T key, Node cur) {
    	if (cur == null) {
    		return cur;
    	} else if (key.compareTo(cur.key) < 0) {
    		cur.left = deleteAux(key, cur.left);
    	} else if (key.compareTo(cur.key) > 0) {
    		cur.right = deleteAux(key, cur.right);
    	} else {
    		// cur is the node to delete
    		Node swap = null;
    		if (cur.left == null && cur.right == null) {
    			swap = cur;
    			cur = null;
    		} else if (cur.left == null) {
    			swap = cur.right;
    			cur = swap;
    		} else if(cur.right == null) {
    			swap = cur.left;
    			cur = swap;
    		} else {
    			swap = inOrder(cur.right);
    			System.out.print(swap.key);
    			cur.key = swap.key;
    			cur.right = deleteAux(swap.key, cur.right);
    		}
    	}
    	
    	if (cur == null) {
    		return cur;
    	}
    	
    	if (imbal(cur) > g) {
    		if (imbal(cur.left) >= 0) {
    			return rotateRight(cur);
    		} else {
    			return rotateLeftRight(cur);
    		}
    	} else if (imbal(cur) < -g) {
    		if (imbal(cur.right) <= 0) {
    			return rotateLeft(cur);
    		} else {
    			return rotateRightLeft(cur);
    		}
    	}
    	
		return cur;
    }
    
    // For this private method, to find the inorder I'm giving a root of the tree and want
    // to find the minimum value in said tree
    private Node inOrder(Node cur) {
    	while (cur.left != null) {
    		cur = cur.left;
    	}
    	
    	return cur;
    }


    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
        if (maxImbalance < 1) {
        	throw new InvalidBalanceException("Error creating a new AVL-G Tree");
        }
        
        root = null;
        g = maxImbalance;
        size = 0;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
    	// Empty Tree
        if (root == null) {
        	root = new Node(null, null, key);
        } else {
        	root = insertAux(key, root);
        }
    
        this.size += 1;
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
        if (size == 0)   {
        	throw new EmptyTreeException("Tried to delete on Empty Tree");
        } else if (search(key) == null) {
        	return null;
        } else {
        	root = deleteAux(key, root);
        	size -= 1;
        	return key;
        }
    }
    
    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
    	if (root == null) {
    		throw new EmptyTreeException("This is empty?>?>?>");
    	}
    	
    	return (foundAux(root, key) != null) ? key : null;
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return g;
    }

    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        return height(root);
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException {
    	if (size == 0) {
    		throw new EmptyTreeException("Can't get Root of Empty Tree");
    	}
        return root.key;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
    	if (size == 0 || size == 1) {
    		return true;
    	}
        return isBSTA(root);
    }
    

    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
    	if (size == 0 || size == 1) {
    		return true;
    	}
        return maxImbalAux(root) <= g;    // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        root = null;
        size = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
    	return size;
    }
}

