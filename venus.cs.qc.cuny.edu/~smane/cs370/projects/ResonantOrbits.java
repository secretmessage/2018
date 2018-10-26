//
// Resonant orbits: animation and tracking
//
// Sateesh R. Mane, copyright 2018
//

import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ResonantOrbits extends Application {
    
    private static final int COUNT = 3000;
    private static final int ORBITS = 30;
    
    private final Rectangle[] nodes = new Rectangle[ORBITS*COUNT];
    private final double[][] x = new double[ORBITS][COUNT];
    private final double[][] p = new double[ORBITS][COUNT];
    private final Color co[] = new Color[ORBITS];
    
    //private final Random random = new Random();
    
    private static final double ks = 0.015 + 0.01*Math.random();
    private static final double eps = 0.04*(Math.random()-0.5);
    private static final double nu = 3.0/7.0 + eps;
    private static final double cc = Math.cos(Math.PI*nu);
    private static final double ss = Math.sin(Math.PI*nu);
    private static final double xmax = 1000;
    private static final long delta_t = 100;
    private long prev = 0;
    private int num = 1;
    
    private void setColors() { 	
    	co[0]= Color.WHITE;
    	co[1]= Color.RED;
    	co[2]= Color.ORANGE;
    	co[3]= Color.YELLOW;
    	co[4]= Color.GREEN;
    	co[5]= Color.BLUE;
    	co[6]= Color.MAGENTA;
    	co[7]= Color.BROWN;
    	co[8]= Color.CYAN;
    	co[9]= Color.PINK;
    	for (int i = 10; i < ORBITS; ++i) {
	    co[i] = co[i-10];
    	}
    }
    
    public void update_xp() {
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
    
    @Override
    public void start(final Stage primaryStage) {
    	setColors();    	
    	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
            for (int i = 0; i < COUNT; i++) {
               	nodes[iorb*COUNT+i] = new Rectangle(1, 1, co[iorb]);
            }
	    x[iorb][0] = (iorb+1)*10.0 + 5.0*Math.random();
	    p[iorb][0] = 50.0*Math.random();
            for (int i = num; i < COUNT; i++) {
            	x[iorb][i] = x[iorb][i-1];
            	p[iorb][i] = p[iorb][i-1];
            }
    	}
    	
        final Scene scene = new Scene(new Group(nodes), 800, 600, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
	
        final double width = 0.5 * primaryStage.getWidth();
        final double height = 0.5 * primaryStage.getHeight();
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {          
            	if (num == 1) {
		    ++num;
		    update_xp();
            	}
            	else {
		    if (now - prev < delta_t) return;
		    if (num < COUNT) ++num;
		    update_xp();
            	}
       		prev = now;            		
		
            	for (int iorb = 0; iorb < ORBITS; ++iorb) {    	
		    for (int i = 0; i< num; i++) {
			final Node node = nodes[iorb*COUNT+i];
			node.setTranslateX(x[iorb][i] + width);
			node.setTranslateY(p[iorb][i]*1.6 + height);
		    }
            	}
            }
        }.start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
