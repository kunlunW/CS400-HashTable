import java.util.Objects;

public class Book {
    private String isbn13;
    private String authors; 
    private String original_publication_year;
    private String title; 
    private String language_code;
    private String average_rating;
    private String cover_type; 
    private String pages;
    
    public Book(String isbn13, String authors, 
            String original_publication_year, String title,
            String language_code, String average_rating, 
            String cover_type, String pages){
        this.isbn13 = isbn13; 
        this.title = title;
        this.authors = authors; 
        this.original_publication_year = original_publication_year;
        this.language_code = language_code; 
        this.average_rating = average_rating;
        this.cover_type = cover_type; 
        this.pages = pages; 
    }

    public String getKey() {
        return this.isbn13;
    }
    
    public void setKey(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    
    @Override
    public String toString() {
        return "ISBN13: "+this.isbn13+"; Book: "+ 
               this.title+", Author: "+this.authors+
               ", Original Publication Year: "+
               this.original_publication_year+
               ", Language: "+this.language_code+", Average Rating: "+
               this.average_rating+", Cover Type: "+this.cover_type+ 
               ", Pages: "+ this.pages;                
    }
}

