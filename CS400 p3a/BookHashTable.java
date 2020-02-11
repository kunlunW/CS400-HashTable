import java.util.ArrayList;
import java.util.LinkedList;




/**
 * Algorithm Explanations:
 * 
 * I decided to use a LinkedList to implement the hash Table.
 * The terminology for using linkedList is separate chaining, and in such a DS 
 * each element of the hash table is a linked list. To store an element in the 
 * hash table you must insert it into a specific linked list. If we encounter a 
 * collision, then we can store both elements in the same linked list. 
 * 
 */


/**
 * HashTable implementation that uses:
 * 
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */
public class BookHashTable implements HashTableADT<String, Book> {
    // declare a linkedList for the HashTable
    private LinkedList<Book>[] hashLinkedList;
    // initial value of the capacity if none is specified
    static final int DEFAULT_CAPACITY = 101;
    // initial load factor threshold if none is specified
    static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;
    // declare a double value to store the LFT
    private double loadFactorThreshold;
    // number of keys 
    private int numKeys;
    // the capacity of the hashTable 
    private int capacity;

    /**
     * REQUIRED default no-arg constructor
     * Uses default capacity and sets load factor threshold 
     * for the newly created hash table.
     */
    public BookHashTable() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
    }

    
    public BookHashTable(int initialCapacity, double loadFactorThreshold) {
        
      
        // accept the initial capacity 
        this.capacity = initialCapacity;
        // accept the initial LFT
        this.loadFactorThreshold = loadFactorThreshold;
        // pass the value of initial capacity to the dimension of the LinkedList 
        hashLinkedList = new LinkedList[initialCapacity];
    }
    
    /**
     * Add the key,value pair to the data structure and increase the number of keys.
     * If key is null, throw IllegalNullKeyException;
     * If key is already in data structure, throw DuplicateKeyException();
     * @param key the key to be added to the hash table
     * @param value the value of the key 
     */
    @Override
    public void insert(String key, Book value) throws IllegalNullKeyException, DuplicateKeyException {
        // declare linkedList for bucket to store potential collisions 
        LinkedList<Book> bucketList;
        
        Book bookToStore = value;
        // check is key is null
        if (key == null) {
            throw new IllegalNullKeyException();
        }
       
       
        // Because the we want to have an integer hash code value of the object(key),
        // we must convert hashCode() to a valid index value, according to my research 
        // online, % doesn't work since hash code might be negative. So it is safer and 
        // much easier to use hashCode & 0x7FFFFFFF. If we just use key%capcity which 
        // might result in such a scenario: -1%10 = -1 which does not make sense for 
        // an index value 
        
        // the constant 0x7FFFFFFF is 0111 1111 1111 1111 1111 1111 1111 1111
        // hash & 0x7FFFFFF will result in a positive int which is what we want 
        // as our hashCode
        // source: https://www.cs.princeton.edu/courses/archive/spring19/cos226/lectures/study/34HashTables.html
        // source: https://stackoverflow.com/questions/49592995
        // we need to calculate the valid index for the key (needs to be positive to make sense)
        // now the hasCode is positive, so we can readily % it to give an positive index 
        int hashIndex = (key.hashCode() & 0x7FFFFFFF) % capacity;
        
        // If LinkedList at the hashIndex is null, we create a new empty bucket linked list
        if (hashLinkedList[hashIndex] == null) {
            bucketList = new LinkedList<>();
            
           // if the LinkedList at the hashIndex is not null, then store the key in that position 
           // to bucketList 
        } 
        else {
            bucketList = hashLinkedList[hashIndex];
        }
        // if there are no keys in the hashTable
        if (numKeys == 0) {
            // add the book to the bucketList
            bucketList.add(bookToStore);
            // And then assign that bucket linkedList to the hashTable Linked List 
            hashLinkedList[hashIndex] = bucketList;
            // increment the number of keys
            numKeys++;
        } 
        // if there are keys in the hashTable
        else {
            // determine if the key is already stored in the hashTableList
            try {
                // if the get() is not throwing an KeyNotFoundException, then the key is already 
              // in the hashTable
                get(key);
                throw new DuplicateKeyException();
                //if KeyNotFoundException is thrown, key to be added is the unique one, which means
                // that we can added the key to the list
            } catch (KeyNotFoundException e) {
                bucketList.add(bookToStore);
                hashLinkedList[hashIndex] = bucketList;
                numKeys++;
                double loadFactor = (double) (numKeys / capacity);
                // Caution!!: we need to check after each insertion if the loadFactor surpassed
                // the LFT, if it did, then resize and rehash the hashTable.
                if (loadFactor >= loadFactorThreshold) {
                    // temporarily store all the key-value pairs in the hashTable, and then
                    // store them in an arrayList (easiest) 
                  ArrayList<Book> tempStorage = new ArrayList<Book>();
                  
                  for (int i = 0; i < hashLinkedList.length; i++) {
                    // first of all, we need to store all the key pairs into the bucket
                    LinkedList<Book> tempBucketStorage = hashLinkedList[i];
                   
                    for (int j = 0; j < tempBucketStorage.size(); j++) {
                      tempStorage.add(tempBucketStorage.get(j));
                    }
                  }
                  ArrayList<Book> tempStorageForResize = tempStorage;
                   
                    reHashing(tempStorageForResize);
                }
            }
        }
    }

   

    /**
     * This function serves to resize the hash Table if the load factor is larger than the LFT
     * @param temp, the arrayList that temporarily holds the original key-value pairs
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     */
   
    private void reHashing(ArrayList<Book> temp) throws IllegalNullKeyException, DuplicateKeyException {
        // we need to double the table size 
        this.capacity = capacity * 2 + 1;
        // update the size of the list to the hashTable
        this.hashLinkedList = new LinkedList[capacity];
        // initialize the numbers of keys to zero
        this.numKeys = 0;
        // insert the key-value pairs to the resized table 
        for (int i = 0; i < temp.size(); i++) {
            insert(temp.get(i).getKey(), temp.get(i));
        }
    }

    /**
     * If key is found, remove the key,value pair from the data structure
     * decrease number of keys.return true. If key is null, throw IllegalNullKeyException
     * If key is not found, return false.
     * @param key, the key to be removed
     * @throws IllegalNullKeyException
     */
    @Override
    public boolean remove(String key) throws IllegalNullKeyException {
        // we declare a bucketList 
        LinkedList<Book> bucketRemoveList;
        // we declare a book variable that stores the book to be removed if any 
        Book bookToRemove;
        // if the key is null, then an exception is thrown
        if (key == null) {
            throw new IllegalNullKeyException();
        }
       
        
        
        // we need to calculate the hashIndex of a particular key using the 
        // algorithm I proposed above 
        int hashIndex = (key.hashCode() & 0x7FFFFFFF) % hashLinkedList.length;
        // if the hashTable is empty which means that the key must not be found
        // we return false 
        if (hashLinkedList[hashIndex] == null) {
            return false;
        } 
        // if the hashValue at the particular key index is not null, then we need to 
        // store that particular value to the bucket LinkedList. we need to check after 
        // we stored the key to the bucket list, the bucketList's size must not be 0;
        // if it is zero, return false;
        else {
            bucketRemoveList = hashLinkedList[hashIndex];
            if (bucketRemoveList.size() == 0) {
                return false;
            }
        }
        // at this point the hashTable List must be be empty and we need to 
        // search the table for the key to be removed. 
        for (int i = 0; i < bucketRemoveList.size(); i++) {
            bookToRemove = bucketRemoveList.get(i);
            if (key.equals(bookToRemove.getKey())) {
                bucketRemoveList.remove(bookToRemove);
                numKeys--;
                return true;
            }
        }
        return false;
    }

    
    /**
     * Returns the value associated with the specified key
     * Does not remove key or decrease number of keys
     * 
     * If key is null, throw IllegalNullKeyException
     * If key is not found, throw KeyNotFoundException().
     * @param key, the key to be searched
     * @throws IllegalNullKeyException
     */
    @Override
    public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
        LinkedList<Book> bucketGetList;// Create LinkedList Reference
        Book bookToGet;
        if (key == null) {
            throw new IllegalNullKeyException();
        }
        // we need to calculate the hashIndex of the particular key using the algorithm 
        // described above 
        int hashIndex = (key.hashCode() & 0x7FFFFFFF) % capacity;
        // if the position at the hashIndex is null, the key is not found, we need to 
        // throw an exception
        if (hashLinkedList[hashIndex] == null) {
            throw new KeyNotFoundException();
        } 
        // if the position at the hashIndex is not null, we need to store the value to the bucketList
        // then we need to double check that the value is properly stored to the bucketList so that 
        // the size of the bucketList must not be zero
        else {
            bucketGetList = hashLinkedList[hashIndex];
            if (bucketGetList.size() == 0) {
                throw new KeyNotFoundException();
            }
        }
        // At this point, the hash table must not be zero, and we need to search the table 
        // to find the key if any 
        for (int i = 0; i < bucketGetList.size(); i++) {
            bookToGet = bucketGetList.get(i);
            if (key.equals(bookToGet.getKey())) {
                return bookToGet;
            }
        }
        throw new KeyNotFoundException();
    }


    /**
     * return the number of keys in the hash table
     */
    @Override
    public int numKeys() {
        return numKeys;
    }

    /**
     * return the load factor threshold of the hash table
     */
    @Override
    public double getLoadFactorThreshold() {
        return loadFactorThreshold;
    }

    /**
     * return the capacity of the hash table
     */
    @Override
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the collision resolution scheme used for this hash table.
     * 5 CHAINED BUCKET: array list of linked lists
     */
    @Override
    public int getCollisionResolutionScheme() {
        return 5;
    }
}
