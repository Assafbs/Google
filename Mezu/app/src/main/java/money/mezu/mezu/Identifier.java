package money.mezu.mezu;

/**
 * Created by asafb on 4/15/2017.
 */

// an abstract class for all identifiers
public abstract class Identifier {

    private long id; // for now; can be replaced with something else.

    public Identifier(long id) {
        super();
        this.id =id;
    }

    public boolean equals(Identifier other){
        if ((this.id == other.id) && (this.getClass() == other.getClass())){
            return true;
        }
        return false;
    }

    public long getId() {
        return this.id;
    }

}
