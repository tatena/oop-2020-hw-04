import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    Bank bank;

    @Test
    public void testSmallSingleThread() throws IOException, InterruptedException {
        bank = new Bank(1);
        bank.processFile("small.txt", 1);
        testAccountsSmall();
    }

    @Test
    public void testSmallMultipleThread() throws IOException, InterruptedException {
        bank = new Bank(10);
        bank.processFile("small.txt", 10);
        testAccountsSmall();
    }

    @Test
    public void testMain5k() throws IOException, InterruptedException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {"5k.txt", "7"};
        Bank.main(args);

        String balances = out.toString();
        testAccounts5k(balances);
    }

    @Test
    public void testBadMain() throws IOException, InterruptedException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = { };
        Bank.main(args);

        String error = out.toString();
        assertTrue(error.contains("Args: transaction-file [num-workers [limit]]"));
    }

    @Test
    public void testMain100kSingle() throws IOException, InterruptedException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(out);

        PrintStream ignored = System.out;
        System.setOut(print);

        String [] args = {"100k.txt"};
        Bank.main(args);

        String balances = out.toString();
        testAccounts5k(balances);
    }


    private void testAccounts5k(String balances) {
        StringTokenizer tk = new StringTokenizer(balances, "\n");

        while (tk.hasMoreTokens()) {
            String currAccount = tk.nextToken();
            assertTrue(currAccount.contains("balance: 1000"));
        }
    }


    private void testAccountsSmall() {
        Map<Integer, Account> accounts = bank.getBalances();
        for (int i = 0; i <accounts.size(); i++) {
            if (i % 2 == 0) {
                assertEquals(999, accounts.get(i).getBalance());
            } else {
                assertEquals(1001, accounts.get(i).getBalance());
            }
            assertEquals(1, accounts.get(i).getTransactions());
        }
    }



}