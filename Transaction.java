// Transaction.java
/*
 (provided code)
 Transaction is just a dumb struct to hold
 one transaction. Supports toString.
*/
public class Transaction {
	public int from;
	public int to;
	public int amount;
	
   	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public String toString() {
		return("from:" + from + " to:" + to + " amt:" + amount);
	}

	@Override
	public boolean equals(Object obj) {
		Transaction trans = (Transaction)obj;
		return this.from == trans.from && this.to == trans.to && this.amount == trans.amount;
	}
}
