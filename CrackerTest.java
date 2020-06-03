import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrackerTest {

    @Test
    public void testSimple() {
        assertEquals("4181eecbd7a755d19fdf73887c54837cbecf63fd",  Cracker.generateMode("molly"));
        assertEquals("86f7e437faa5a7fce15d1ddcb9eaeaea377667b8",  Cracker.generateMode("a"));
        assertEquals("adeb6f2a18fe33af368d91b09587b68e3abcb9a7",  Cracker.generateMode("fm"));
        assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3",  Cracker.generateMode("xyz"));

    }


}