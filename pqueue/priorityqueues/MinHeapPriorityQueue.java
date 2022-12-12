package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */

import pqueue.exceptions.*;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.MinHeap;
import java.util.Arrays;
import java.util.Iterator;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- YOUR NAME HERE ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/

	class PQNode implements Comparable<PQNode>{
		int priority;
		T element;
		
		PQNode(int p, T d) {
			priority = p;
			element = d;
		}
		
		@Override
		public int compareTo(PQNode o) {
			return this.priority - o.priority;
		}
		
		public T getElement() {
			return element;
		}
	}
	
	private ArrayMinHeap<PQNode> mh;



	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		mh = new ArrayMinHeap<PQNode>();
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority <= 0) {
			throw new InvalidPriorityException("");
		}
		
		mh.insert(new PQNode(priority, element));
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!		
		try {
			return mh.deleteMin().element;
		} catch (EmptyHeapException e) {
			// TODO Auto-generated catch block
			throw new EmptyPriorityQueueException("");
		}
		
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		try {
			return mh.getMin().element;
		} catch (EmptyHeapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new EmptyPriorityQueueException("");
		}
	}

	@Override
	public int size() {
		return mh.size();
	}

	@Override
	public boolean isEmpty() {
		return mh.isEmpty();
	}


	@Override
	public Iterator<T> iterator() {
		return new MHPQIterator(mh);
	}
	
	class MHPQIterator<T extends Comparable<T>> implements Iterator<T> {
		Iterator<PQNode> cop;
      
		// initialize pointer to head of the list for iteration
		public MHPQIterator(ArrayMinHeap<PQNode> guy)
		{
			cop = guy.iterator();
		}
			
		// returns false if next element does not exist
		public boolean hasNext()
		{
			return cop.hasNext();
		}
			
		// return current data and pdate pointer
		public T next()
		{
			return (T) cop.next().element;
		}
	}
	

}
