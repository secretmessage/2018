// Demo program to use threads 
// ** no shared resources **
// ** each thread sums integers 1 through n **

public class ThreadDemo {

    public static void main(String[] args) {
	int num_threads = 6;
	MyThread myt[] = new MyThread[num_threads];
	for (int i = 0; i < num_threads; ++i) {
	    myt[i] = new MyThread(i+1);
	    myt[i].start();
	}
	System.out.println("all threads spawned");

	// join the threads, this must be in a try-catch block
	for (int i = 0; i < num_threads; ++i) {
	    try {
		if (myt[i].isAlive()) {
		    myt[i].join();
		}
	    } 
	    catch (Exception e) {}
	}

	// get results from the thread objects
	for (int i = 0; i < num_threads; ++i) {
	    int s = myt[i].sum;
	    System.out.println("thread #, sum = " + i + "   " + s);
	}

	System.out.println("end main");
    }
}

class MyThread extends Thread {
    public int sum = 0;
    public int len = 0;

    MyThread(int n) { 
	len = n; 
    }

    @Override 
    public void run() {
	sum = 0;
	for (int i = 1; i <= len; ++i) {
	    sum += i;
	}
    }
}
