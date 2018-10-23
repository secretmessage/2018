
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class ThreadWrite {
    private static final int num_threads = 10;
    private static WriteOutput wo;

    public static void main(String[] args) {
	try {
	    // open file in overwrite mode
	    // clear out the file
	    FileWriter fw = new FileWriter("out_java.txt", false); 
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	wo = new WriteOutput();
	MyWrite mw[] = new MyWrite[num_threads];
	for (int i = 0; i < num_threads; ++i) {
	    mw[i] = new MyWrite("thread " + (i+1), wo);
	    mw[i].start();
	}
	for (int i = 0; i < num_threads; ++i) {
	    try {
		mw[i].th.join();
	    } 
	    catch (Exception e) {}
	}
	System.out.println("end main");
    }
}

class WriteOutput {
    public void printConsole(String s) {
	System.out.println(s);
    }
    public void appendFile(BufferedWriter bw, String s, boolean b, long n, long i) {
	try {
	    bw.append(s + "\n");
	    if (true == b) {
		bw.append("n is prime: " + n + "\n");
		System.out.println("n is prime: " + n);
	    }
	    else {
		bw.append("n is composite: " + n + ", factor = " + i + "\n");
	    }
	    bw.append("\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}


class MyWrite implements Runnable {
    Thread th;
    private String name;
    private WriteOutput wo;
    private static final long scale = 1 << 28;
    
    MyWrite(String s, WriteOutput obj) { 
	name = s; 
	wo = obj;
    }
    public void start() {
	if (th == null) {
	    th = new Thread(this, name);
	    th.start();
	}
    }
    @Override 
    public void run() {
	// synchronize write to console
	synchronized(wo) {
	    wo.printConsole(th.toString());
	}

	// test for prime
	long n = (long)(Math.random()*scale + scale);
	n = 2*n+1;  // odd integer
	boolean b = true;
	long i = 0;
	for (i = 3; i*i <= n; i += 2) {
	    if (n % i == 0) {
		b = false;
		break;
	    }
	}

	// synchronize write to file
	synchronized(wo) {
	    try {
		boolean append_mode = true;
		FileWriter fw = new FileWriter("out_java.txt", append_mode); 
		BufferedWriter bw = new BufferedWriter(fw);
		String s = th.getName() + " : " + th.toString();
		wo.appendFile(bw, s, b, n, i);
		bw.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
