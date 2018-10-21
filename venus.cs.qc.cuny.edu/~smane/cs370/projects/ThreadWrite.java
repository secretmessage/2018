
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class ThreadWrite {
    private static final int num = 3;

    public static void main(String[] args) {
	try {
	    FileWriter fw=  new FileWriter("out_java.txt", false); // overwrite mode
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	int num_threads = 5;
	MyWrite mw[] = new MyWrite[num_threads];
	for (int i = 0; i < num_threads; ++i) {
	    mw[i] = new MyWrite("thread " + (i+1));
	    mw[i].start();
	}
	for (int i = 0; i < num_threads; ++i) {
	    System.out.println(mw[i].th.toString());
	    for (int j = 0; j < num; ++j)
	        System.out.println("    " + j);
	    try {
		mw[i].th.join();
	    } 
	    catch (Exception e) {}
	}
	System.out.println("end main");
    }
}

class MyWrite implements Runnable {
    Thread th;
    private String name;
    private final boolean append_mode = true;
    private final int num = 3;
    
    MyWrite(String s) { 
	name = s; 
    }
    public void start() {
	if (th == null) {
	    th = new Thread(this, name);
	    th.start();
	}
    }
    @Override 
    public void run() {
	try {
	    FileWriter fw=  new FileWriter("out_java.txt", append_mode); // append mode
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.append(th.getName() + " : " + th.toString() + "\n");
	    for (int j = 0; j < num; ++j)
		bw.append("    " + j + "\n");
	    bw.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
