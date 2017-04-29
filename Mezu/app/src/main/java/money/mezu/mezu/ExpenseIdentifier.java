package money.mezu.mezu;

import java.math.BigInteger;

/**
 * Created by asafb on 4/15/2017.
 */

public class ExpenseIdentifier extends Identifier {
    public ExpenseIdentifier(BigInteger id) {
        super(id);
    }

    @Override
    public boolean equals(Identifier other) {
        return super.equals(other);
    }
}

