package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.PRQuadTree;

import java.util.Collection;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author --- YOUR NAME HERE! ---
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
	PRQuadNode[] children;
	PRQuadNode parent;
    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        children = new PRQuadNode[4];
    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
        // Check what quadrant the point should be in
    	if (p.coords[1] >= centroid.coords[1]) {
    		// North
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    			PRQuadNode child = children[1];
    			if (child == null) {
    				// Calculate new centroid
    				KDPoint nCentroid = new KDPoint(new int[]{(int) Math.pow(2, this.k) / 4 + centroid.coords[0], 
    														  (int) Math.pow(2, this.k) / 4 + centroid.coords[1]});	
    				this.children[1] = new PRQuadBlackNode(nCentroid, k-1, bucketingParam, p);
    			} else {
    				this.children[1] = children[1].insert(p, k - 1);
    			}
    			return this;
    		} else {
    			// West
    			PRQuadNode child = children[0];
    			if (child == null) {
    				// Calculate new centroid
    				KDPoint nCentroid = new KDPoint(new int[]{(int) ((int) centroid.coords[0] - (Math.pow(2, this.k) / 4)), 
    														  (int) Math.pow(2, this.k) / 4 + centroid.coords[1]});	
    				this.children[0] = new PRQuadBlackNode(nCentroid, k-1, bucketingParam, p);
    			} else {
    				this.children[0] = children[0].insert(p, k - 1);
    			}
    			return this;
    		}
    	} else {
    		// South
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    			PRQuadNode child = children[3];
    			if (child == null) {
    				// Calculate new centroid
    				KDPoint nCentroid = new KDPoint(new int[]{(int) Math.pow(2, this.k) / 4 + centroid.coords[0], 
    														  (int) ((int) centroid.coords[1] - (Math.pow(2, this.k) / 4))});	
    				this.children[3] = new PRQuadBlackNode(nCentroid, k-1, bucketingParam, p);
    			} else {
    				this.children[3] = children[3].insert(p, k - 1);
    			}
    			return this;
    		} else {
    			// West
    			PRQuadNode child = children[2];
    			if (child == null) {
    				// Calculate new centroid
    				KDPoint nCentroid = new KDPoint(new int[]{(int) (centroid.coords[0] - (Math.pow(2, this.k) / 4)), 
    														(int) (centroid.coords[1] - (Math.pow(2, this.k) / 4))});	
    				this.children[2] = new PRQuadBlackNode(nCentroid, k-1, bucketingParam, p);
    			} else {
    				this.children[2] = children[2].insert(p, k - 1);
    			}
    			return this;
    		}
    	}
    	
    	// If it's a white node allocate a new black node
    	
    	// If not insert into black node and set the child to be so
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
    	if (p.coords[1] >= centroid.coords[1]) {
    		// North
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    		
    			this.children[1] = children[1].delete(p);
    			
    			if (this.count() <= this.bucketingParam) {
    				PRQuadNode ret = new PRQuadBlackNode(centroid, k, bucketingParam);
    				for (PRQuadNode cur: children) {
    					if (cur != null) {
    						PRQuadBlackNode anal = (PRQuadBlackNode) cur;
    						for (KDPoint cr: anal.getPoints()) {
    							if (cr != null)
    								ret.insert(cr, bucketingParam);
    						}
    					}
    				}
    				return ret;
    			}
    			
    			return this;
    		} else {
    			// West
    			this.children[0] = children[0].delete(p);
    			
    			if (this.count() <= this.bucketingParam) {
    				PRQuadNode ret = new PRQuadBlackNode(centroid, k, bucketingParam);
    				for (PRQuadNode cur: children) {
    					if (cur != null) {
    						PRQuadBlackNode anal = (PRQuadBlackNode) cur;
    						for (KDPoint cr: anal.getPoints()) {
    							if (cr != null)
    								ret.insert(cr, bucketingParam);
    						}
    					}
    				}
    				return ret;
    			}
    			
    			return this;
    		}
    	} else {
    		// South
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    			this.children[3] = children[3].delete(p);
    			
    			if (this.count() <= this.bucketingParam) {
    				PRQuadNode ret = new PRQuadBlackNode(centroid, k, bucketingParam);
    				for (PRQuadNode cur: children) {
    					if (cur != null) {
    						PRQuadBlackNode anal = (PRQuadBlackNode) cur;
    						for (KDPoint cr: anal.getPoints()) {
    							if (cr != null)
    								ret.insert(cr, bucketingParam);
    						}
    					}
    				}
    				return ret;
    			}
    			
    			return this;
    		} else {
    			// West
    			this.children[2] = children[2].delete(p);
    			
    			if (this.count() <= this.bucketingParam) {
    				PRQuadNode ret = new PRQuadBlackNode(centroid, k, bucketingParam);
    				for (PRQuadNode cur: children) {
    					if (cur != null) {
    						PRQuadBlackNode anal = (PRQuadBlackNode) cur;
    						for (KDPoint cr: anal.getPoints()) {
    							if (cr != null)
    								ret.insert(cr, bucketingParam);
    						}
    					}
    				}
    				return ret;
    			}
    			
    			return this;
    		}
    	}
    }

    @Override
    public boolean search(KDPoint p){
    	// Check what quadrant the point should be in
    	if (p.coords[1] >= centroid.coords[1]) {
    		// North
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    			PRQuadNode child = children[1];
    			if (child == null) {
    				return false;
    			}
    			return child.search(p);
    		} else {
    			// West
    			PRQuadNode child = children[0];
    			if (child == null) {
    				return false;
    			}
    			return child.search(p);
    		}
    	} else {
    		// South
    		if (p.coords[0] >= centroid.coords[0]) {
    			// East 
    			PRQuadNode child = children[3];
    			if (child == null) {
    				return false;
    			}
    			return child.search(p);
    		} else {
    			// West
    			PRQuadNode child = children[2];
    			if (child == null) {
    				return false;
    			}
    			return child.search(p);
    		}
    	}
        
    }

    @Override
    public int height(){
    	int htNw = 0;
        if (children[0] != null) {
        	htNw = children[0].height();
        } 
        
        int htNe = 0;
        if (children[1] != null) {
        	htNe = children[1].height();
        }
        
        int htSw = 0;
        if (children[2] != null) {
        	htSw = children[2].height();
        }
        
        int htSe = 0;
        if (children[3] != null) {
        	htSe = children[3].height();
        }
        int ht = 1 + Math.max(Math.max(htNw, htNe), Math.max(htSw, htSe));
        
        return ht;
    }

    @Override
    public int count(){
    	int htNw = 0;
        if (children[0] != null) {
        	htNw = children[0].count();
        } 
        
        int htNe = 0;
        if (children[1] != null) {
        	htNe = children[1].count();
        }
        
        int htSw = 0;
        if (children[2] != null) {
        	htSw = children[2].count();
        }
        
        int htSe = 0;
        if (children[3] != null) {
        	htSe = children[3].count();
        }
        int ct = htNw + htNe + htSw + htSe;
        return ct;
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren() {
    	return children;
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
        for (PRQuadNode cur: children) {
        	if (cur != null) {
        		if (cur.doesQuadIntersectAnchorRange(anchor, range)) {
        			cur.range(anchor, results, range);
        		}
        	}
        }
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
    	NNData<KDPoint> close = new NNData(n.getBestGuess(), n.getBestDist());
    	for (PRQuadNode cur: children) {
        	if (cur != null) {
        		if (close.getBestGuess() == null || cur.doesQuadIntersectAnchorRange(anchor, close.getBestDist())) {
        			NNData<KDPoint> cd = cur.nearestNeighbor(anchor, close);
        			if (close.getBestGuess() == null || cd.getBestDist() <= close.getBestDist()) {
        				close.update(cd.getBestGuess(), cd.getBestDist());
        			}
        		}
        	}
        }
    	return close;
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
    	for (PRQuadNode cur: children) {
        	if (cur != null) {
        		if (queue.first() == null || cur.doesQuadIntersectAnchorRange(anchor, queue.first().euclideanDistance(anchor))) {
        			cur.kNearestNeighbors(k, anchor, queue);
        		}
        	}
        }
    }
}

