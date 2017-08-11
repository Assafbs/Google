package money.mezu.mezu;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by asafb on 4/15/2017.
 */

// an abstract class for all identifiers
public abstract class Identifier {
    private BigInteger id; // for now; can be replaced with something else.

    public Identifier(BigInteger id) {
        super();
        this.id = id;
    }

    public boolean equals(Identifier other) {
        return (Objects.equals(this.id, other.id)) && (this.getClass() == other.getClass());
    }

    public BigInteger getId() {
        return this.id;
    }

}
