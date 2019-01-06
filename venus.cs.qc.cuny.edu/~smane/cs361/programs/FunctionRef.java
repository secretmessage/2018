//
// Java "function reference" use trapezoid rule as example
//
// Sateesh R. Mane, copyright 2018
//

package func_ref;


// "Math Library"

abstract interface MathLibraryFunc {
    public double f(double x);
}

final class MathLibrary {
    public static double Trapezoid(MathLibraryFunc func, double a, double b, int n) {
	if (n <= 0) return 0;
	double h = (b-a)/n;
	double fa = func.f(a);
	double fb = func.f(b);
	double sum = (fa+fb)*0.5;
	for (int i = 1; i < n; ++i) {
	    double x = a + i*h;
	    sum += func.f(x);
	}
	return h*sum;
    }

    public static double Midpoint(MathLibraryFunc func, double a, double b, int n) {
	if (n <= 0) return 0;
	double h = (b-a)/n;
	double sum = 0;
	double x = a + 0.5*h;
	for (int i = 0; i < n; ++i) {
	    sum += func.f(x);
	    x += h;
	}
	return h*sum;
    }
}

//-------------------------------------
// "user code" to use math library

final class Linear implements MathLibraryFunc {
    @Override
    public double f(double x) { return x; }
}

final class Square implements MathLibraryFunc {
    @Override
    public double f(double x) { return x*x; }
}

public class FunctionRef {
    public static void main(String[] args) {
	Linear lin = new Linear();
	Square sq = new Square();
	double a = 0;
	double b = 1;	
	System.out.println("Linear funtion:");
	for (int n = 1; n < 10; ++n) {
	    double tz = MathLibrary.Trapezoid(lin, a, b, n); 
	    double md = MathLibrary.Midpoint(lin, a, b, n); 
	    System.out.println("n, linear = " + n + "   " + tz + "   " + md);
	}
	System.out.println("");
	System.out.println("Quadratic funtion:");
	for (int n = 1; n < 10; ++n) {
	    double tz = MathLibrary.Trapezoid(sq, a, b, n); 
	    double md = MathLibrary.Midpoint(sq, a, b, n); 
	    System.out.println("n, quadratic = " + n + "   " + tz + "   " + md);
	}
    }
}
