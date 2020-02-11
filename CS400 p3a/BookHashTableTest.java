
import org.junit.After;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/** 
 * Test HashTable class implementation to ensure that required 
 * functionality works for all cases.
 */
public class BookHashTableTest {

    // Default name of books data file
    public static final String BOOKS = "books.csv";

    // Empty hash tables that can be used by tests
    static BookHashTable bookObject;
    static ArrayList<Book> bookTable;

    static final int INIT_CAPACITY = 2;
    static final double LOAD_FACTOR_THRESHOLD = 0.49;
       
    static Random RNG = new Random(0);  // seeded to make results repeatable (deterministic)

    /** Create a large array of keys and matching values for use in any test */
    @BeforeAll
    public static void beforeClass() throws Exception{
        bookTable = BookParser.parse(BOOKS);
    }
    
    /** Initialize empty hash table to be used in each test */
    @BeforeEach
    public void setUp() throws Exception {
        
         bookObject = new BookHashTable(INIT_CAPACITY,LOAD_FACTOR_THRESHOLD);
    }

    /** Not much to do, just make sure that variables are reset     */
    @AfterEach
    public void tearDown() throws Exception {
        bookObject = null;
    }

    private void insertMany(ArrayList<Book> bookTable, int j) 
        throws IllegalNullKeyException, DuplicateKeyException {
        for (int i = 0; i < j; i++ ) {
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        }
    }
    private void removeMany(ArrayList<Book> bookTable, int j) 
        throws IllegalNullKeyException {
        for (int i = 0; i < j; i++ ) {
            bookObject.remove(bookTable.get(i).getKey());
        } 
    }

    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is empty upon initialization
     */
    
    @Test
    public void test000_collision_scheme() {
        if (bookObject == null)
        	fail("Gg");
    	int scheme = bookObject.getCollisionResolutionScheme();
        if (scheme < 1 || scheme > 9) 
            fail("collision resolution must be indicated with 1-9");
    }
    

    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is empty upon initialization
     */
    @Test
    public void test000_IsEmpty() {
        //"size with 0 entries:"
        assertEquals(0, bookObject.numKeys());
    }

    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is not empty after adding one (key,book) pair
     * @throws DuplicateKeyException 
     * @throws IllegalNullKeyException 
     */
    @Test
    public void test001_IsNotEmpty() throws IllegalNullKeyException, DuplicateKeyException {
    	bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
        String expected = ""+1;
        //"size with one entry:"
        assertEquals(expected, ""+bookObject.numKeys());
    }
    
    /** IMPLEMENTED AS EXAMPLE FOR YOU 
    * Test if the hash table  will be resized after adding two (key,book) pairs
    * given the load factor is 0.49 and initial capacity to be 2.
    */
    
    @Test 
    public void test002_Resize() throws IllegalNullKeyException, DuplicateKeyException {
    	bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
    	int cap1 = bookObject.getCapacity(); 
    	bookObject.insert(bookTable.get(1).getKey(),bookTable.get(1));
    	int cap2 = bookObject.getCapacity(); 	
        //"size with one entry:"
        assertTrue(cap2 > cap1 & cap1 ==2);
    }
    
    /**
     * Test that get throws the proper exceptions
     */
    
    @Test 
    public void test003_get_throws_correct_exceptions() throws DuplicateKeyException {
        try {
            bookObject = new BookHashTable(101,0.75);
            bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
            bookObject.get(null);
            fail("IllegalNullKeyExceptions Should be thrown");
        }
        catch(IllegalNullKeyException e) {
            
        }
        catch(KeyNotFoundException e) {
            fail("KeyNotFoundException should not be thrown");
        }
        
        try {
            bookObject.get(bookTable.get(1).getKey());      
            fail("KeyNotFoundException is not thrown");
        }
        catch(IllegalNullKeyException e) {
            fail("IllegalNullKeyException should not be thrown");
        }
        catch(KeyNotFoundException e) {
    
        }
    }
    
    
    /**
     * Test exception throwing of insert method
     */
    @Test 
    public void test004_insert_throws_correct_exceptions() {
        try {
            bookObject.insert(null, bookTable.get(0));
            fail("IllegalNullKeyException not thrown");  
        }
        catch(IllegalNullKeyException e) {
            
        }
        catch(DuplicateKeyException e) {
            fail("DuplicateKeyExcepion should not be thrown");
        }
        try {
            bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
            bookObject.insert(bookTable.get(1).getKey(),bookTable.get(1));
            bookObject.insert(bookTable.get(1).getKey(),bookTable.get(1));
            fail("DuplicateKeyException is not thrown");  
        }
        catch(IllegalNullKeyException e) {
            fail("IllegalNullKeyException should not be thrown");
        }
        catch(DuplicateKeyException e) { 
        }
    }
    
    /**
     * Test that remove exceptions
     */
    @Test
    public void test005_remove_throws_IllegalNullKeyException() {
        try {
           bookObject.remove(null);
           fail("Exception is not thrown");
        }
        catch(IllegalNullKeyException e) {
        }
    }
    
    /**
     * Test that numKeys() method returns the right value 
     */
    @Test
    public void test005_check_numKeys_after_mutiple_inserts_and_deletes() throws Exception{
        insertMany(bookTable,10);
        if(bookObject.numKeys() != 10) {    
            fail("wrong numKeys");
        }
       removeMany(bookTable,10);         
       if(bookObject.numKeys() != 0) {    
           fail("wrong numKeys");
       }
       insertMany(bookTable,20);
       if(bookObject.numKeys() != 20) {    
           fail("wrong numKeys");
       }        
    }
    
    
    /**
     * Test getLoadFactorThreshold() functionality
     */
    @Test
    public void test006_getLoadThreshold() {
        bookObject = new BookHashTable(10, 0.5);
        if(bookObject.getLoadFactorThreshold() != 0.5) {
            fail("wrong LTF");
        }
    }
    
    /**
     * Test getCapacity() functionality that the getCapacity()
     */
    @Test
    public void test007_getCapacity() {
        bookObject = new BookHashTable(10, 0.5);
        if(bookObject.getCapacity() != 10) {
            fail("wrong capacity");
        }
    }
    
    /**
     * test remove() functionality that remove should return true when a valid
     * item is successfully removed 
     */
    @Test
    public void test008_remove_returns_correct_boolean() throws Exception{
        bookObject = new BookHashTable(100,0.8);
       insertMany(bookTable, 10);
       if(!bookObject.remove(bookTable.get(5).getKey())) {
           fail("fail");
       }
       if(bookObject.remove(bookTable.get(20).getKey())) {
           fail("fail");
       }
    }
    
    /**
     * Test that rehashing would give a correct re-size.
     * 
     */
    @Test
    public void test009_resize() throws DuplicateKeyException, IllegalNullKeyException,
    KeyNotFoundException {
        bookObject = new BookHashTable(10,0.8);
        insertMany(bookTable,9);
        if(bookObject.getCapacity() != 21) {
            fail("fail");
        }
        for(int i = 0; i < bookObject.numKeys(); i++) {
            if(!bookObject.get(bookTable.get(i).getKey()).equals(bookTable.get(i))) {
                fail("fail");
            }
        }
    }
    
    
    
    /**
     * Insert a big number and get the key
     */
    @Test
    public void test0010_insert_500_keys() throws DuplicateKeyException, IllegalNullKeyException, 
    KeyNotFoundException {
        bookObject = new BookHashTable(50,.8);
        insertMany(bookTable, 500);
        for(int i = 0; i < bookObject.numKeys(); i ++) {
            if(!bookObject.get(bookTable.get(i).getKey()).equals(bookTable.get(i))){
                fail("fail");
            }
        }
    }
}
    
  