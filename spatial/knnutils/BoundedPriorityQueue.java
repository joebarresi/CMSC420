package spatial.knnutils;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import spatial.exceptions.UnimplementedMethodException;


/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/jasonfillipou/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T>{

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */
	
	// Implementation will be a linked list.
	// Easily get head


	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */
	
	class LLNode implements Comparable{
		double priority;
		T element;
		LLNode next, prev;
		
		public LLNode(double priority, T element) {
			this.priority = priority;
			this.element = element;
			next = null;
			prev = null;
		}

		@Override
		public int compareTo(Object o) {
			LLNode other = (BoundedPriorityQueue<T>.LLNode) o;
			double res = other.priority - this.priority;
			if (res == 0) {
				return 0;
			}
			

			return res > 0 ? 1 : 0;
		}
				
	}

	int capacity;
	int length;
	LLNode head;
	LLNode tail;
	public boolean modificationFlag;
	
	
	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		if (size <= 0) {
			throw new IllegalArgumentException("");
		}
		
		head = null;
		tail = null;
		length = 0;
		capacity = size;
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, double priority) {
		// Empty List
		modificationFlag = true;
		LLNode toIns = new LLNode(priority, element);
		length += 1;
		if (head == null) {
			head = toIns;
			tail = toIns;
		} else if (priority >= head.priority) {
			toIns.next = head;
			head.prev = toIns;
			head = toIns;
		} else {
			LLNode cur = head;

			while (cur.next != null && toIns.priority - cur.next.priority < 0) {
				cur = cur.next;
			}

			toIns.next = cur.next;
			if (cur.next != null) {
				cur.next.prev = toIns;
			} else {
				tail = toIns;
			}
			toIns.prev = cur;
			cur.next = toIns;
		}
		
		if (length > capacity) {
			head.next.prev = null;
			head = head.next;
			length--;
		}
	}

	@Override
	public T dequeue() {
		modificationFlag = true;
		T retVal;
		if (length > 1) {
			retVal = tail.element;
			LLNode newT = tail.prev;
			newT.next = null;
			tail = newT;
			length--;
		} else if (length == 1) {
			retVal = tail.element;
			head = null;
			tail = null;
			length--;
		} else {
			retVal = null;
		}
		
		BoundedPriorityQueue<T>.LLNode guy = head;
		while (guy != null) {
			guy = guy.next;
		}
		return retVal;
	}

	@Override
	public T first() {
		return (length == 0) ? null : tail.element;
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		return (length == 0) ? null : head.element;
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element)
	{
		for (T cur : this) {
			if (cur.equals(element))
				return true;
		}
		
		return false;
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0;
	}
	
	class LLIter implements Iterator<T> {
		
		private LLNode current;
		private boolean fullCircle;

		public LLIter(){
			current = tail;
			modificationFlag = false;
			fullCircle = false;
		}

		public boolean hasNext(){
			return current != null && !fullCircle;
		}

		public T next()  throws ConcurrentModificationException, NoSuchElementException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): List was modified while traversing it through iterator.");
			if(fullCircle)
				throw new NoSuchElementException("next(): Iterator exhausted elements.");
			T currData = current.element;
			current = current.prev;
			if(current == null)				
				fullCircle = true;
			return currData;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new LLIter();
	}
}
