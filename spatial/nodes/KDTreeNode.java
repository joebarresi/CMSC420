package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  ---- YOUR NAME HERE! -----
 *
 * @see spatial.trees.KDTree
 */
public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */
    private KDTreeNode parent;  

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */


    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        this.p = new KDPoint(p);
        height = 0;
        left = null;
        right = null;
        parent = null;
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public void insert(KDPoint pIn, int currDim, int dims){
        if (this.p.coords[currDim % dims] <= pIn.coords[currDim % dims]) {
        	if (right != null) {
        		right.insert(pIn, currDim + 1, dims);
        	} else {
        		right = new KDTreeNode(pIn);
        	}
        	right.parent = this;
        } else {
        	if (left != null) {
        		left.insert(pIn, currDim + 1, dims);
        	} else {
        		left = new KDTreeNode(pIn);
        	}
        	left.parent = this;
        }
        
        int rtHt = 0;
        int lfHt = 0;
        if (left != null) {
        	lfHt = left.height;
        }
        if (right != null) {
        	rtHt = right.height;
        }
        
        height = 1 + Math.max(rtHt, lfHt);  
    }

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
        // See if the node == nodetobedeleted
    	if (pIn.equals(this.p)) {
    		if (left == null && right == null) {
    			//delete node
    			if (pIn.coords[currDim % dims] >= parent.p.coords[currDim % dims]) {
    				parent.right = null;
    			} else {
    				parent.left = null;
    			}
    			return this;
    		} else if (right != null) {
    			// find inorder successor for current dimension
    			KDTreeNode inorder = right.minimum(currDim, dims);
    			KDTreeNode copy = this;
    			if (pIn.coords[currDim % dims] >= parent.p.coords[currDim % dims]) {
	    			this.left = inorder.left;
	    			this.right = inorder.right;
	    			this.p = new KDPoint(inorder.p);
    				parent.right = this;
    			} else {
	    			this.left = inorder.left;
	    			this.right = inorder.right;
	    			this.p = new KDPoint(inorder.p);
    				parent.left = this;
    			}
	    			// recursively delete
    			right.delete(inorder.p, currDim, dims);
    			return copy;
    		} else {
    			//find min value in current dimesnion
    			//make the left st the right st
    			//recursively delte	
    			
    			// find inorder successor for current dimension
    			KDTreeNode min = minimum(currDim, dims);
    			KDTreeNode copy = this;
    			// recursively delete
    			this.right = this.left;
    			this.left = null;
    			this.p = new KDPoint(min.p);
    			right.delete(min.p, currDim, dims);
    			return copy;
    		}
    	} else {
    		// figure out which way to recurse
        	if (pIn.coords[currDim % dims] >= this.p.coords[currDim % dims]) {
        		return right.delete(pIn, currDim + 1, dims);
        	} else {
        		return left.delete(pIn, currDim + 1, dims);
        	}
    	}
    }

    private KDTreeNode minimum(int currDim, int dims) {
		// TODO Auto-generated method stub
    	KDTreeNode node = this;
		
    	if (left != null && right != null) {
    		KDTreeNode min = (p.coords[currDim % dims] <= right.minimum(currDim, dims).p.coords[currDim % dims]) ? this : right;
    		return (min.p.coords[currDim % dims] <= left.minimum(currDim, dims).p.coords[currDim % dims]) ? min : left;
    	} else if (left != null) {
    		// Just left
    		return (p.coords[currDim % dims] <= left.minimum(currDim, dims).p.coords[currDim % dims]) ? this : left;
    	} else if (right != null) {
    		//just right
    		return (p.coords[currDim % dims] <= right.minimum(currDim, dims).p.coords[currDim % dims]) ? this : right;
    	} else {
    		// leaf
    		return this;
    	}
	}

	/**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public  boolean search(KDPoint pIn, int currDim, int dims){
        if (pIn.equals(p)) {
        	return true;
        } 
        
        if (this.p.coords[currDim % dims] <= pIn.coords[currDim % dims]) {
        	if (right == null) {
        		return false;
        	}
        	return right.search(pIn, currDim + 1, dims);
        } else {
        	if (left == null) {
        		return false;
        	}
        	return left.search(pIn, currDim + 1, dims);
        }
    }

    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
     *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
        // Check if this node is in range
    	double distance = anchor.euclideanDistance(p);
    	
    	if (distance <= range) {
    		results.add(p);
    	}
    	// Find best way to recurse
    	if (this.p.coords[currDim % dims] <= anchor.coords[currDim % dims]) {
    		// Node is in the right
    		if (right != null) {
    			right.range(anchor, results, range, currDim + 1, dims);
    		}
    		
    		if (left != null && anchor.coords[currDim % dims] - range <= p.coords[currDim % dims]) {
    			left.range(anchor, results, range, currDim + 1, dims);
    		}
    	} else {
    		// Node is in the left
    		if (left != null) {
    			left.range(anchor, results, range, currDim + 1, dims);
    		}
    		
    		if (right != null && anchor.coords[currDim % dims] + range >= p.coords[currDim % dims]) {
    			right.range(anchor, results, range, currDim + 1, dims);
    		}
    	}
    }


    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){
    	// Check if this node is closest
    	double distance = anchor.euclideanDistance(p);
    	
    	if ((n.getBestDist() < 0 || distance <= n.getBestDist()) && !anchor.equals(p)) {
    		n.update(p, distance);
    	}
    	// Find best way to recurse
    	if (this.p.coords[currDim % dims] <= anchor.coords[currDim % dims]) {
    		// Node is in the right
    		if (right != null) {
    			n = right.nearestNeighbor(anchor, currDim + 1, n, dims);
    		}
    		
    		if ((n.getBestDist() < 0 || anchor.coords[currDim % dims] - n.getBestDist() <= p.coords[currDim % dims])
    			&& left != null)  {
    			n = left.nearestNeighbor(anchor, currDim + 1, n, dims);
    		} 
    		
    
    	} else {
    		// Node is in the left
    		if (left != null) {
    			n = left.nearestNeighbor(anchor, currDim + 1, n, dims);
    		}
    		
    		if ((n.getBestDist() < 0 || anchor.coords[currDim % dims] + n.getBestDist() >= p.coords[currDim % dims]) && right != null) {
    			n = right.nearestNeighbor(anchor, currDim + 1, n, dims);
    		}
    	}
    	
    	return n;
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public  void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
    	// Check if this node is closest
    	double distance = anchor.euclideanDistance(p);
    	
    	if (!anchor.equals(p)) {
    		queue.enqueue(p, distance);
    	}
    	// Find best way to recurse
    	if (this.p.coords[currDim % dims] <= anchor.coords[currDim % dims]) {
    		// Node is in the right
    		if (right != null) {
    			right.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		}
    		
    		if (anchor.coords[currDim % dims] - queue.last().euclideanDistance(anchor) <= p.coords[currDim % dims]
    			&& left != null)  {
    			left.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		} 
    		
    
    	} else {
    		// Node is in the left
    		if (left != null) {
    			left.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		}
    		
    		if (anchor.coords[currDim % dims] + queue.last().euclideanDistance(anchor) >= p.coords[currDim % dims] && right != null) {
    			right.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		}
    	}
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))+1</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        return height;
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        return new KDPoint(this.p);
    }

    public KDTreeNode getLeft(){
       return left;
    }

    public KDTreeNode getRight(){
        return right;
    }
}
