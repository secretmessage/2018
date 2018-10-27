//
// Java animation: resonant orbits animation and tracking
//
// Sateesh R. Mane, copyright 2018
//

import java.awt.EventQueue;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class OrbitTracking extends JFrame {

    public OrbitTracking() {
        add(new Screen());
        setResizable(false);
        pack();
        
        setTitle("Orbit Tracking");    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }
    
    public static void main(String[] args) {        
        EventQueue.invokeLater(() -> {
		JFrame ex = new OrbitTracking();
		ex.setVisible(true);
	    });
    }
}


final class Screen extends JPanel implements Runnable {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Thread animator;
    
    private static final int COUNT = 100000;
    private static final int ORBITS = 100;
    
    private final double[][] x = new double[ORBITS][COUNT];
    private final double[][] p = new double[ORBITS][COUNT];
    private final Color co[] = new Color[ORBITS];

    private static final double ks = 0.015 + 0.01*Math.random();
    private static final double eps = 0.04*(Math.random()-0.5);
    private static final double nu = 3.0/7.0 + eps;
    private static final double cc = Math.cos(Math.PI*nu);
    private static final double ss = Math.sin(Math.PI*nu);
    private static final double xmax = 1000;
    private int num = 1;

    private void setColors() { 	
    	co[0]= Color.WHITE;
    	co[1]= Color.RED;
    	co[2]= Color.YELLOW;
    	co[3]= Color.GREEN;
    	co[4]= Color.ORANGE;
    	co[5]= Color.BLUE;
    	co[6]= Color.MAGENTA;
    	co[7]= Color.LIGHT_GRAY;
    	co[8]= Color.PINK;
    	co[9]= Color.CYAN;
    	for (int i = 10; i < ORBITS; ++i) {
	    co[i] = co[i-10];
    	}
    }
    
    private void update_xp() {
    	if (num > COUNT) return;
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
            for (int i = 1; i < num; i++) {
            	double xx = x[iorb][i-1]*cc + p[iorb][i-1]*ss;
            	double pp = p[iorb][i-1]*cc - x[iorb][i-1]*ss;
            	pp -= ks*xx*xx;
            	x[iorb][i] = xx*cc + pp*ss;
            	p[iorb][i] = pp*cc - xx*ss;
            	if (Math.abs(x[iorb][i]) > xmax) break;
            }
    	}            		        	
    }
    
    public Screen() {
        init();
    }
    
    private void init() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
	
    	setColors();    	
    	num = 1;
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
            x[iorb][0] = (iorb+1)*5.0*(0.5 + Math.random());
            p[iorb][0] = 100.0*Math.random() - 50.0;
            for (int i = num; i < COUNT; i++) {
            	x[iorb][i] = x[iorb][i-1];
            	p[iorb][i] = p[iorb][i-1];
            }
    	}
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        updateDisplay(g);
    }
    
    private void updateDisplay(Graphics g) {
    	if (num < COUNT) ++num;
    	update_xp();
    	// draw the points
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
	    g.setColor(co[iorb]);
	    for (int i = 0; i< num; i++) {
		double xx = x[iorb][i]*2 + WIDTH*0.5;
		double pp = p[iorb][i]*3.2 + HEIGHT*0.5;
    	    	g.fillRect((int)xx, (int)pp, 1, 1);
	    }
    	}		
        Toolkit.getDefaultToolkit().sync();
    }
    
    @Override
    public void run() {
        while (true) {
            repaint();
        }
    }
}
