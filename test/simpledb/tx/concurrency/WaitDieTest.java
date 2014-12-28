package simpledb.tx.concurrency;

import simpledb.file.Block;
import junit.framework.TestCase;

public class WaitDieTest extends TestCase {
	
	private static Block block = new Block("My File", 0);
	private static ConcurrencyMgr cm1 = new ConcurrencyMgr(1);
	private static ConcurrencyMgr cm2 = new ConcurrencyMgr(2);
	private static ConcurrencyMgr cm3 = new ConcurrencyMgr(3);
	private static ConcurrencyMgr cm4 = new ConcurrencyMgr(4);
	private static ConcurrencyMgr cm5 = new ConcurrencyMgr(5);

	public static void main(String[] args) {		
		test();
	}
	
	public static void test() {	
		try {			
			cm3.sLock(block);			
			new Thread() { public void run() {
				try {
					Thread.sleep(3000);
					cm2.xLock(block);
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();	

			new Thread() { public void run() {
				try {
					Thread.sleep(6000);
					cm4.xLock(block);
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();			
			
			//release 3
			new Thread() { public void run() {
				try {
					Thread.sleep(10000);
					cm3.release();

				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();		
			
			//release 2
			new Thread() { public void run() {
				try {
					Thread.sleep(15000);
					cm2.release();
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();

			new Thread() { public void run() {
				try {
					Thread.sleep(20000);
					cm5.sLock(block);
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();			
			
			new Thread() { public void run() {
				try {
					Thread.sleep(23000);
					cm1.sLock(block);
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();	
			
			//release 5
			new Thread() { public void run() {
				try {
					Thread.sleep(27000);
					cm5.release();
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();		
			
			
			
//release 1

			new Thread() { public void run() {
				try {
					Thread.sleep(30000);
					cm1.release();
				} catch (InterruptedException e) {}
				catch (LockAbortException e) {}

			}}.start();
			
		}
		catch (LockAbortException e) {}	

	}
	
}
