package money.mezu.mezu;

import java.math.BigInteger;
import java.util.Objects;

// An abstract class for all identifiers
public abstract class Identifier {
    private BigInteger id;

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
