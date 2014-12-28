package simpledb.tx.concurrency;

import simpledb.file.Block;
import java.util.*;

/**
 * The concurrency manager for the transaction.
 * Each transaction has its own concurrency manager. 
 * The concurrency manager keeps track of which locks the 
 * transaction currently has, and interacts with the
 * global lock table as needed. 
 * @author Edward Sciore
 */
public class ConcurrencyMgr {
   
	private static LockTable locktbl = new LockTable();
	private Map<Block,String> locks  = new HashMap<Block,String>();

	private int txnum;

	public ConcurrencyMgr(int txnum) {
		super();
		this.txnum = txnum;
	}

	public void sLock(Block blk) {
		if (locks.get(blk) == null) {
			locktbl.sLock(blk, txnum);
			locks.put(blk, "S");
		}
	}
	
	public void xLock(Block blk) {
		if (!hasXLock(blk)) {
			sLock(blk);
			locktbl.xLock(blk, txnum);
			locks.put(blk, "X");
		}
	}

	
	public void release() {
		for (Block blk : locks.keySet()) {
			locktbl.unlock(blk, txnum);
			System.out.println("Released: " + blk + "TX:" + txnum);
		}
		locks.clear();
	}

	private boolean hasXLock(Block blk) {
		String locktype = locks.get(blk);
		return locktype != null && locktype.equals("X");
	}


	public int getTxnum() {
		return txnum;
	}
	public void setTxnum(int txnum) {
		this.txnum = txnum;
	}

}
	

