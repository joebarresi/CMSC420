package pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import pqueue.exceptions.UnimplementedMethodException;
import pqueue.priorityqueues.LinearPriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * <p>{@link ArrayMinHeap} is a {@link MinHeap} implemented using an internal array. Since heaps are <b>complete</b>
 * binary trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per {@code null} pointer in a linked implementation.</p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a <b>contiguous storage</b> implementation based on a linear {@link java.util.Collection}
 * like an {@link java.util.ArrayList} or a {@link java.util.Vector} (but *not* a {@link java.util.LinkedList} because it's *not*
 * contiguous storage!). or a raw Java array. We provide an array for you to start with, but if you prefer, you can switch it to a
 * {@link java.util.Collection} as mentioned above. </p>
 *
 * @author -- YOUR NAME HERE ---
 *
 * @see MinHeap
 * @see LinkedMinHeap
 * @see demos.GenericArrays
 */

public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* *****************************************************************************************************************
	 * This array will store your data. You may replace it with a linear Collection if you wish, but
	 * consult this class' 	 * JavaDocs before you do so. We allow you this option because if you aren't
	 * careful, you can end up having ClassCastExceptions thrown at you if you work with a raw array of Objects.
	 * See, the type T that this class contains needs to be Comparable with other types T, but Objects are at the top
	 * of the class hierarchy; they can't be Comparable, Iterable, Clonable, Serializable, etc. See GenericArrays.java
	 * under the package demos* for more information.
	 * *****************************************************************************************************************/
	private Object[] data;
	private int size;
	int cm = 0;

	/* *********************************************************************************** *
	 * Write any further private data elements or private methods for LinkedMinHeap here...*
	 * *************************************************************************************/
	private void extendArray(){
		Object[] newArray = new Object[data.length + 50];

		for(int i = 0; i < data.length; i++) {
			newArray[i] = data[i];
		}

		this.data = newArray;
	}

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the data structure with some default
	 * capacity you can choose.
	 */
	public ArrayMinHeap(){
		this.data = new Object[100];
		this.size = 0;
		cm = 0;
	}

	/**
	 *  Second, non-default constructor which provides the element with which to initialize the heap's root.
	 *  @param rootElement the element to create the root with.
	 */
	public ArrayMinHeap(T rootElement){
		this.data = new Object[100];
		this.size = 1;
		data[0] = rootElement;
		cm = 0;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon copy of the {@link MinHeap} parameter.
	 *
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	public ArrayMinHeap(MinHeap<T> other){
		throw new UnimplementedMethodException();
	}

	/**
	 * Standard {@code equals()} method. We provide it for you: DO NOT ERASE! Consider its implementation when implementing
	 * {@link #ArrayMinHeap(MinHeap)}.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 * @see #ArrayMinHeap(MinHeap)
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while(itThis.hasNext())
			if(!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}

	@Override
	public void insert(T element) {
		if (size >= data.length) 
			extendArray();

		data[size] = element;
		size++;

		int insIdx = size - 1;
		int parIdx = (insIdx - 1) / 2;

		while (((T) data[parIdx]).compareTo((T) data[insIdx]) > 0 && parIdx >= 0) {
			T temp = (T) data[insIdx];
			data[insIdx] = data[parIdx];
			data[parIdx] = temp;
			insIdx = parIdx;
			parIdx = (insIdx - 1) / 2;
		}
		cm += 1;
	}

	@Override
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (size == 0) {
			throw new EmptyHeapException("Heap's Empty");
		} 
		T ret = null;
		if (size == 1) {
			ret = (T) data[0];
			size--;
		} else {
			ret = (T) data[0];
			data[0] = data[size - 1];
			size --;

			int curIdx = 0;
			int leftIdx = 1 , rightIdx = 2;
			while (true) {
				if (leftIdx < size && ((T) data[curIdx]).compareTo((T) data[leftIdx]) > 0 && (rightIdx >= size || ((T)data[rightIdx]).compareTo((T) data[leftIdx]) >= 0)) {
					T temp = (T) data[leftIdx];
					data[leftIdx] = data[curIdx];
					data[curIdx] = temp;
					curIdx = leftIdx;
				} else if (rightIdx < size && ((T)data[curIdx]).compareTo((T) data[rightIdx]) > 0 && ((T)data[leftIdx]).compareTo((T) data[rightIdx]) > 0) {
					T temp = (T) data[rightIdx];
					data[rightIdx] = data[curIdx];
					data[curIdx] = temp;
					curIdx = rightIdx;
				} else {
					break;
				}
				leftIdx = 2*curIdx + 1;
				rightIdx = 2*curIdx + 2;
			}
		}
		cm += 1;
		return ret;
	}

		@Override
	public T getMin() throws EmptyHeapException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (size == 0) {
			throw new EmptyHeapException("Heap's Empty");
		}

		return (T) data[0];
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Standard equals() method.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 */


	@Override
	public Iterator<T> iterator() {
		return new MHIterator<T>(this);
	}
	
	class MHIterator<T extends Comparable<T>> implements Iterator<T> {
		int current;
		Object[] data;
		int size;
		int currCm;
      
		// initialize pointer to head of the list for iteration
		public MHIterator(ArrayMinHeap<T> guy)
		{
			current = 0;
			Object[] a2 = new Object[guy.size];
			for(int i = 0; i < guy.size; i++) {
				a2[i] = guy.data[i];
			}
			
			Arrays.sort(a2);
			this.data = a2;
			this.size = guy.size;
			currCm = cm;
		}
			
		// returns false if next element does not exist
		public boolean hasNext()
		{
			if (currCm != cm) {
				throw new ConcurrentModificationException("");
			} else {
				return current < size;
			}
			
		}
			
		// return current data and pdate pointer
		public T next()
		{
			if (currCm != cm) {
				throw new ConcurrentModificationException("");
			} else {
				T dat = (T) data[current];
				current += 1;
				return dat;
			}
			
		}
	}
}