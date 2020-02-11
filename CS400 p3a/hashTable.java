import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Algorithm Explanations:
 * 
 * I decided to use a LinkedList to implement the hash Table. The terminology for using linkedList
 * is separate chaining, and in such a DS each element of the hash table is a linked list. To store
 * an element in the hash table you must insert it into a specific linked list. If we encounter a
 * collision, then we can store both elements in the same linked list.
 * 
 */

/**
 * HashTable implementation that uses:
 * 
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */

public class hashTable implements HashTableADT<String, Book> {

  static final int DEFAULT_CAPACITY = 101;
  // 
  static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;
  // declare a linkedList for the HashTable
  private LinkedList<KeyValue>[] hashList;
  private double loadFactorThreshold;
  private int numKeys;
  private int capacity;

  // inner class 
  private class KeyValue {
    String key;
    Book value;

    KeyValue(String key, Book value) {
      this.key = key;
      this.value = value;
    }
   
    private String getKey() {
      return this.key;
    }
    private Book getValue() {
      return this.value;
    }
  }

  /**
   * REQUIRED default no-arg constructor Uses default capacity and sets load factor threshold for
   * the newly created hash table.
   */
  public hashTable() {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
  }

  
  @SuppressWarnings("unchecked")
  public hashTable(int initialCapacity, double loadFactorThreshold) {
    
    hashList = new LinkedList[initialCapacity];
  
    this.loadFactorThreshold = loadFactorThreshold;
    this.capacity = initialCapacity;
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
    if (key == null) {
      throw new IllegalNullKeyException();
    }
    
    LinkedList<KeyValue> bucketList;// Create LinkedList reference
    
    KeyValue keyVal = new KeyValue(key, value);// Create KeyValue reference
    // Calculate hash index
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
    if (hashList[hashIndex] == null) {
     
      bucketList = new LinkedList<>();
    } else {
     
      bucketList = hashList[hashIndex];
    }
    if (numKeys == 0) {
      
      bucketList.add(keyVal);
      hashList[hashIndex] = bucketList;
      numKeys++;
    } else {
      try {
        
        get(key);
        
        throw new DuplicateKeyException();
      } catch (KeyNotFoundException e) {
        
        bucketList.add(keyVal);
        hashList[hashIndex] = bucketList;
        numKeys++;
        if ((double) numKeys / capacity >= loadFactorThreshold) {
          
          ArrayList<KeyValue> keyValues = this.getAllKeyValues();
          
          reHash(keyValues);
        }
      }
    }
  }

  /**
   * helper to store all the elements in the hash table to a temp
   * 
   * @return keyValues
   */
  private ArrayList<KeyValue> getAllKeyValues() {
    ArrayList<KeyValue> temp = new ArrayList<KeyValue>();
    for (int i = 0; i < hashList.length; i++) {
      LinkedList<KeyValue> bucketTemp = hashList[i];
      if (bucketTemp == null || bucketTemp.size() == 0) {
       
        continue;
      } else {
        
        for (int j = 0; j < bucketTemp.size(); j++) {
          temp.add(bucketTemp.get(j));
        }
      }
    }
    return temp;
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
      this.hashL = new LinkedList[capacity];
      // initialize the numbers of keys to zero
      this.numKeys = 0;
      // insert the key-value pairs to the resized table 
      for (int i = 0; i < temp.size(); i++) {
          insert(temp.get(i).getKey(), temp.get(i));
      }
  }


  /**
   * If the key is present,removes a key value pair from the hash table
   * 
   * @param key- key to remove from the hash table
   * @throws IllegalNullKeyException- if key is null
   * @return true- if key is removed, false if not
   */
  @Override
  public boolean remove(String key) throws IllegalNullKeyException {
    if (key == null) {
      throw new IllegalNullKeyException();
    }
    LinkedList<KeyValue> bucket;// Create LinkedList Reference
    KeyValue keyValue;// Create KeyValue reference
    // Calculate hashIndex for key
    int hashIndex = hash(key);
    if (hashList[hashIndex] == null) {
      // Nothing stored at that index in the hash table
      return false;
    } else {
      bucket = hashList[hashIndex];
      if (bucket.size() == 0) {
        // LinkedList is empty no keys stored
        return false;
      }
    }
    // Linked List not empty need to search through the list
    // and remove key value pair if its present
    for (int i = 0; i < bucket.size(); i++) {
      keyValue = bucket.get(i);
      if (key.equals(keyValue.getKey())) {
        bucket.remove(keyValue);
        numKeys--;
        return true;
      }
    }
    // Key was not found in the linked list, so nothing was removed
    return false;
  }

  /**
   * Looks for a key in the hash table, if found returns value associated with key, if not found
   * throws exception
   * 
   * @param key - key to look for in hash table
   * @throws DuplicateKeyException if key already in hash table
   * @throws IllegalNullKeyException if key is null
   * @return book(value) associated with the key if key is stored in table
   */
  @Override
  public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
    if (key == null) {
      throw new IllegalNullKeyException();
    }
    LinkedList<KeyValue> bucket;// Create LinkedList Reference
    KeyValue keyValue;// Create KeyValue reference
    // Calculate hashIndex for key
    int hashIndex = hash(key);
    if (hashList[hashIndex] == null) {
      // Nothing stored at that index in the hash table yet
      throw new KeyNotFoundException();
    } else {
      bucket = hashList[hashIndex];
      if (bucket.size() == 0) {
        // LinkedList is empty no keys stored
        throw new KeyNotFoundException();
      }
    }
    // LinkedList is not empty,
    // iterate through the list to try and find key
    for (int i = 0; i < bucket.size(); i++) {
      keyValue = bucket.get(i);
      if (key.equals(keyValue.getKey())) {
        // key is found in the LinkedList
        return keyValue.getValue();
      }
    }
    // After iterating through linked list key was not found
    throw new KeyNotFoundException();
  }

  /**
   * Returns the number of key value pairs stored in the hash table
   * 
   * @return numKeys
   */
  @Override
  public int numKeys() {
    return numKeys;
  }

  /**
   * Returns the load factor threshold(number that causes resizing) of the hash table
   * 
   * @return loadFactorThreshold
   */
  @Override
  public double getLoadFactorThreshold() {
    return loadFactorThreshold;
  }

  /**
   * Returns the current capacity of the hash table
   * 
   * @return capacity
   */
  @Override
  public int getCapacity() {
    return capacity;
  }

  /**
   * Returns the collision resolution scheme used for this hash table. 5 CHAINED BUCKET: array of
   * linked lists
   * 
   * @return 5
   */
  @Override
  public int getCollisionResolutionScheme() {
    return 5;
  }
}
