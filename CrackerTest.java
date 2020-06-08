import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CrackerTest {

    @Test
    public void testGenerate() throws NoSuchAlgorithmException {
        assertEquals("4181eecbd7a755d19fdf73887c54837cbecf63fd",  Cracker.generateMode("molly"));
        assertEquals("86f7e437faa5a7fce15d1ddcb9eaeaea377667b8",  Cracker.generateMode("a"));
        assertEquals("adeb6f2a18fe33af368d91b09587b68e3abcb9a7",  Cracker.generateMode("fm"));
        assertEquals("66b27417d37e024c46526c2f6d358a754fc552f3",  Cracker.generateMode("xyz"));

    }

    @Test
    public void testCracker1() throws NoSuchAlgorithmException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {"86f7e437faa5a7fce15d1ddcb9eaeaea377667b8", "2"};
        Cracker.main(args);
        String res = out.toString();
        assertTrue(res.contains("a"));
    }

    @Test
    public void testGenerateMain() throws NoSuchAlgorithmException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {"a"};
        Cracker.main(args);
        String res = out.toString();
        assertTrue(res.contains("86f7e437faa5a7fce15d1ddcb9eaeaea377667b8"));
    }

    @Test
    public void testBadMain() throws NoSuchAlgorithmException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {};
        Cracker.main(args);
        String res = out.toString();
        assertTrue(res.contains("Args: target length [workers]"));
    }

    @Test
    public void testCracker2() throws NoSuchAlgorithmException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {"66b27417d37e024c46526c2f6d358a754fc552f3", "3", "4"};
        Cracker.main(args);
        String res = out.toString();
        assertTrue(res.contains("xyz"));
    }






}