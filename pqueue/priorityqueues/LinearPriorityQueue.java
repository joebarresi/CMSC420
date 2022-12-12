package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import pqueue.exceptions.*;
import pqueue.fifoqueues.FIFOQueue;
import pqueue.heaps.ArrayMinHeap;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- YOUR NAME HERE ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/
	class PQNode {
		T data;
		int priority;

		PQNode(int p, T d) {
			priority = p;
			data = d;
		}
	}

	private ArrayList<LinearPriorityQueue<T>.PQNode> nodes;
	int size;
	int capacity;
	int cm;


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		nodes = new ArrayList<PQNode>();
		size = 0;
		capacity = 100;
		cm = 0;
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is less than 1.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (capacity < 1) {
			throw new InvalidCapacityException("True");
		}

		nodes = new ArrayList<PQNode>();
		size = 0;
		this.capacity = capacity;
		cm = 0;
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority <= 0) {
			throw new InvalidPriorityException("");
		}
		
		if (size == 0) {
			nodes.add(new PQNode(priority, element));
		} else if (priority < nodes.get(0).priority) {
			nodes.add(0, new PQNode(priority, element));
		} else {
			boolean added = false; 
			
			for(int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).priority > priority) {
					nodes.add(i, new PQNode(priority, element));
					added = true;
				}
				
				if (added) {
					break;
				}
			}
			if(!added) {
				nodes.add(new PQNode(priority, element));
			}
		}
		
		cm += 1;
		size += 1;
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (size == 0) {
			throw new EmptyPriorityQueueException("Empty - can't dequeu");
		}

		size --;
		cm += 1;
		return nodes.remove(0).data;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		
		if (size == 0) {
			throw new EmptyPriorityQueueException("Empty - can't dequeu");
		}
		
		return nodes.get(0).data;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}


	@Override
	public Iterator<T> iterator() {
		return new PQIterator<T>(this);
	}

	class PQIterator<T> implements Iterator<T> {
		int current;
		int currCm;
		ArrayList<LinearPriorityQueue<T>.PQNode> l;
      
		// initialize pointer to head of the list for iteration
		public PQIterator(LinearPriorityQueue<T> guy)
		{
			current = 0;
			l = guy.nodes;
			currCm = cm;
		}
			
		// returns false if next element does not exist
		public boolean hasNext()
		{
			if(currCm != cm) {
				throw new ConcurrentModificationException("");
			} else {
				return l.size() > current;
			}
			
		}
			
		// return current data and update pointer
		public T next()
		{
			if(currCm != cm) {
				throw new ConcurrentModificationException("");
			} else {
				T data = l.get(current).data;
				current += 1;
				return data;
			}
		}
	}
}