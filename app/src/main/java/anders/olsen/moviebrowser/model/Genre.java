package anders.olsen.moviebrowser.model;

/**
 * Class representing a Genre from TMDB.
 *
 * @author Anders Engen Olsen
 **/
public class Genre {

    private int id;
    private String genre;

    public Genre(int id, String genre) {
        this.id = id;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
