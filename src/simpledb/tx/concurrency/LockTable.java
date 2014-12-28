package simpledb.tx.concurrency;

import simpledb.file.Block;
import java.util.*;

/**
 * The lock table, which provides methods to lock and unlock blocks.
 * If a transaction requests a lock that causes a conflict with an
 * existing lock, then that transaction is placed on a wait list.
 * There is only one wait list for all blocks.
 * When the last lock on a block is unlocked, then all transactions
 * are removed from the wait list and rescheduled.
 * If one of those transactions discovers that the lock it is waiting for
 * is still locked, it will place itself back on the wait list.
 * @author Edward Sciore
 */
class LockTable {
   
	private Map<Block,List<Integer>> locks = 
			new HashMap<Block,List<Integer>>();


	public synchronized void sLock(Block blk, int txnum) {
		try {
			while (hasXlock(blk)) {
				checkForAbort(blk, txnum);
				System.out.println("wait öncesi");
				wait();
			}
			List<Integer> txs = getLockList(blk);
			txs.add(txnum);
			locks.put(blk, txs);
			System.out.println("Slock OK. B:" + blk + ", TX:" + txnum);
		}
		catch(InterruptedException e) {
			throw new LockAbortException();
		}
	}


	public synchronized void xLock(Block blk, int txnum) {

		try {
			while (hasOtherSLocks(blk)) {
				checkForAbort(blk, txnum);
				wait();
			}
			List<Integer> txs = new ArrayList<Integer>();
			txs.add(-txnum);
			locks.put(blk, txs);
			System.out.println("Xlock OK. B:" + blk + ", TX:" + txnum);
		}
		catch(InterruptedException e) {
			throw new LockAbortException();
		}
	}

	public synchronized void unlock(Block blk, Integer txnum) {
		List<Integer> txs = getLockList(blk);
		if (txs.size() > 2) {
			txs.remove(txnum);
			locks.put(blk, txs);
		}
		if (txs.size() == 2) {
			txs.remove(txnum);
			locks.put(blk, txs);
			notifyAll();
		}
		else {
			locks.remove(blk);
			notifyAll();
		}
	}
	private boolean hasXlock(Block blk) {
		List<Integer> txs = getLockList(blk);
		return (txs.size() > 0 && txs.get(0) < 0);
	}
	private boolean hasOtherSLocks(Block blk) {
		List<Integer> txs = getLockList(blk);
		return txs.size() > 1;
	}
	private void checkForAbort(Block blk, int txnum) {
		List<Integer> txs = getLockList(blk);
		for (int n : txs)
			if (n < txnum)
				throw new LockAbortException();
	}
	private List<Integer> getLockList(Block blk) {
		List<Integer> txs = locks.get(blk);
		return (txs == null) ? new ArrayList<Integer>() : txs;
	}

	
}


