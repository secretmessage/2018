//
// Java animation: resonant orbits animation and tracking
//
// Sateesh R. Mane, copyright 2018
//

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class OrbitTracking extends JFrame {

    public OrbitTracking() {
    	add(new Screen());
        setResizable(false);
        pack();
        
        setTitle("Orbit Tracking");    
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
    }
    
    public static void main(String[] args) {        
        EventQueue.invokeLater(() -> {
		JFrame ex = new OrbitTracking();
		ex.setVisible(true);
	    });
    }
}


final class Screen extends JPanel 
implements Runnable {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Thread animator;
    
    private static final int COUNT = 100000;
    private static final int ORBITS = 200;
    
    private final double[][] x = new double[ORBITS][COUNT];
    private final double[][] p = new double[ORBITS][COUNT];
    private final Color co[] = new Color[ORBITS];

    private static final Font font = new Font("TimesRoman", Font.BOLD, 18);
    //private static final Font font = new Font("Helvetica", Font.BOLD, 18);
    private static double ks = 0.015 + 0.01*Math.random();
    private static double ko = 1.0e-4*(1.0 + 0.5*Math.random());
    private static double eps = 0.04*(Math.random()-0.5);
    private static double nu = 3.0/7.0 + eps;
    private static double cc = Math.cos(Math.PI*nu);
    private static double ss = Math.sin(Math.PI*nu);
    private static final double xmax = 1000;
    private int num = 1;
    private double scale = 1;
    private double xorig = 0;
    private double porig = 0;

    enum Multipole { SEXTUPOLE, OCTUPOLE, COMBINED }
    private Multipole multipole = Multipole.SEXTUPOLE;

    // callback for mouse event
    /*
    public void mouse_Released(MouseEvent evt) {
    	if (evt.getButton() == MouseEvent.BUTTON1)
	    scale *= 1.05;
    	else if (evt.getButton() == MouseEvent.BUTTON3)
	    scale /= 1.05;
    }    
    */

    // callback for keypad event
    /*
    public void keyReleased(KeyEvent evt) {
    	int keyCode = evt.getKeyCode();
    	switch (keyCode) {
    	default:
	    break;
    	case KeyEvent.VK_ESCAPE:
	    SwingUtilities.getWindowAncestor(this).dispose();
	    break;
    	case KeyEvent.VK_0: 
	    xorig = 0;
	    porig = 0;
	    scale = 1;
	    break;
    	case KeyEvent.VK_1: 
	    multipole = Multipole.SEXTUPOLE;
	    init();
	    break;
    	case KeyEvent.VK_2: 
	    multipole = Multipole.OCTUPOLE;
	    init();
	    break;
    	case KeyEvent.VK_3: 
	    multipole = Multipole.COMBINED;
	    init();
	    break;
    	case KeyEvent.VK_LEFT:
	    xorig += 5;
	    break;
    	case KeyEvent.VK_RIGHT:
	    xorig -= 5;
	    break;
    	case KeyEvent.VK_UP:
	    porig += 5;
	    break;
    	case KeyEvent.VK_DOWN:
	    porig -= 5;
	    break;
    	case KeyEvent.VK_S:
	    save();
	    break;
    	}
    }
    */
    
    /*
    private void save() {
    	BufferedImage paintImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	Graphics g = paintImage.createGraphics();
	g.drawImage(paintImage, 0, 0, null);
	updateDisplay(g);
	try {
	    ImageIO.write(paintImage, "PNG", new File("c:/sateesh/orbits.png"));
	    //ImageIO.write(paintImage, "JPG", new File("c:/sateesh/orbits.jpg"));
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }
    */

    private void setColors() { 	
    	co[0]= Color.WHITE;
    	co[1]= Color.RED;
    	co[2]= Color.GREEN;
    	co[3]= Color.YELLOW;
    	co[4]= Color.BLUE;
    	co[5]= Color.ORANGE;
    	co[6]= Color.MAGENTA;
    	co[7]= Color.LIGHT_GRAY;
    	co[8]= Color.PINK;
    	co[9]= Color.CYAN;
    	for (int i = 10; i < ORBITS; ++i) {
	    co[i] = co[i-10];
    	}
    }
    
    private void update_xp() {
    	if ((num < 2) || (num > COUNT)) return;
	int i = num - 1;
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
	    if (Math.abs(x[iorb][i-1]) > xmax) continue;
	    double xx = x[iorb][i-1]*cc + p[iorb][i-1]*ss;
	    double pp = p[iorb][i-1]*cc - x[iorb][i-1]*ss;
	    switch (multipole) {
	    default:
	    case SEXTUPOLE:
		pp -= ks*xx*xx;
		break;            		
	    case OCTUPOLE:
		pp -= ko*xx*xx*xx;
		break;            		
	    case COMBINED:
		pp -= ks*xx*xx;
		pp -= ko*xx*xx*xx;
		break;            		
	    }
	    x[iorb][i] = xx*cc + pp*ss;
	    p[iorb][i] = pp*cc - xx*ss;
	}
    }
    
    public Screen() {
    	init();
    }
    
    private void init() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);	
	setFocusable(true);

	xorig = 0;
	porig = 0;
	scale = 1;
        setColors();    	
	ks = 0.015 + 0.01*Math.random();
	ko = 1.0e-4*(1.0 + 0.5*Math.random());
	eps = 0.04*(Math.random()-0.5);
        switch (multipole) {
        default:
        case SEXTUPOLE:
    	    nu = 3.0/7.0 + eps;
	    break;
        case OCTUPOLE:
    	    nu = 3.0/10.0 + eps;
	    break;
        case COMBINED:
    	    nu = 3.0/16.0 + eps;
	    break;
        }
	cc = Math.cos(Math.PI*nu);
	ss = Math.sin(Math.PI*nu);
    	
    	num = 1;
	double sign = 1;
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
	    if (iorb <= 10) {
		x[iorb][0] = -2.0*(4*iorb+1);
		p[iorb][0] = 0.0;
	    }
	    else {
		x[iorb][0] = 2.0*(iorb+1)*(0.5 + Math.random());
		p[iorb][0] = 100.0*Math.random() - 50.0;
	    }
	    x[iorb][0] *= sign;
	    p[iorb][0] *= sign;
	    sign = -sign;
            for (int i = num; i < COUNT; ++i) {
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
    	if (num < COUNT) {
	    ++num;
	    update_xp();
	}
    	// display text
    	g.setFont(font);
	g.setColor(Color.LIGHT_GRAY);
	double inu = (int)(nu*1e4)/1e4;
	double iks = (int)(ks*1e8)/1e4;
	double iko = (int)(ko*1e8)/1e4;
    	switch (multipole) {
    	default:
    	case SEXTUPOLE:
	    g.drawString("sextupole map", 20, 30);
	    g.drawString("nu = " + inu, 20, 50);
	    g.drawString("ks = " + iks, 20, 70);
	    break;            		
    	case OCTUPOLE:
	    g.drawString("octupole map", 20, 30);
	    g.drawString("nu = " + inu, 20, 50);
	    g.drawString("ko = " + iko, 20, 70);
	    break;            		
    	case COMBINED:
	    g.drawString("combined sextupole & octupole map", 20, 30);
	    g.drawString("nu = " + inu, 20, 50);
	    g.drawString("ks = " + iks, 20, 70);
	    g.drawString("ko = " + iko, 20, 90);
	    break;            		
    	}
	
    	// draw the points
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
	    g.setColor(co[iorb]);
	    for (int i = 0; i< num; ++i) {
		double xx = (xorig + x[iorb][i])*scale*2 + WIDTH*0.5;
		double pp = (porig + p[iorb][i])*scale*3.2 + HEIGHT*0.5;
		int ix = (int)(xx);
		int ip = (int)(pp);
		g.fillRect(ix, ip, 1, 1);
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
