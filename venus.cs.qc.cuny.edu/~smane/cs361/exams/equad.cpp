#include <vector>
#include <cmath>
#include <complex>

class Node 
{
public:
  Node() { x = 0; y = 0; V = 0; bdy = false; }
  double x;
  double y;
  double V;
  bool bdy;
};

class ElectrostaticQuadrupole
{
public:
  ElectrostaticQuadrupole(int n, int k) : _halfedge(5.0) {
    _n = n;
    _off = -k;
    allocate();
    init();
  }

  ~ElectrostaticQuadrupole() { clear(); }

private:
  void allocate() {
    _nodes = new Node*[_n+1];
    for (int i = 0; i <= _n; ++i) {
      _nodes[i] = new Node[_n+1];
    }
  }

  void clear() {
    for (int i = 0; i <= _n; ++i) {
      delete [] _nodes[i];
    }
    delete [] _nodes;
    _n = 0;
  }

  void init() {
    for (int i = 0; i <= _n; ++i) {
      bool bx = ((i==0) || (i==_n)) ? true : false;
      double x = -_halfedge + 2.0*_halfedge*double(i)/double(_n);
      Node * nx = _nodes[i];
      for (int j = 0; j <= _n; ++j) {
	bool by = ((j==0) || (j==_n)) ? true : false;
	double y = -_halfedge + 2.0*_halfedge*double(j)/double(_n);
	Node & nxy = nx[j];
	nxy.x = x;
	nxy.y = y;
	nxy.V = 0;
	nxy.bdy = (bx && by);
      }
    }
  }

  void set(double a) { 

    for (int i = 0; i <= _n; ++i) {
      //double x = -_halfedge + 2.0*_halfedge*double(i)/double(_n);
      Node * nx = _nodes[i];
      Node & bottom = nx[0];
      Node & top = nx[_n];
      double &x = top.x;
      top.V = 0.0;
      bottom.V = 0.0;
      top.bdy = true;
      bottom.bdy = true;
    }

    for (int i = 0; i <= _n; ++i) {
      Node * nx = _nodes[i];
      int j = _n/20 + _off;
      Node & bottom = nx[j];
      Node & top = nx[_n-j];
      double &x = top.x;
      if (std::abs(x) <= 0.5*a) {
	top.V = -100.0;
	bottom.V = -100.0;
	top.bdy = true;
	bottom.bdy = true;
      }
    }

    for (int j = 0; j <= _n; ++j) {
      //double y = -_halfedge + 2.0*_halfedge*double(j)/double(_n);
      Node * left = _nodes[0];
      Node * right = _nodes[_n];
      Node & lj = left[j];
      Node & rj = right[j];
      double &y = lj.y;
      lj.V = 0.0;
      rj.V = 0.0;
      lj.bdy = true;
      rj.bdy = true;
    }

    for (int j = 0; j <= _n; ++j) {
      //double y = -_halfedge + 2.0*_halfedge*double(j)/double(_n);
      int i = _n/20 + _off;
      Node * left = _nodes[i];
      Node * right = _nodes[_n-i];
      Node & lj = left[j];
      Node & rj = right[j];
      double &y = lj.y;
      if (std::abs(y) <= 0.5*a) {
	lj.V = 100.0;
	rj.V = 100.0;
	lj.bdy = true;
	rj.bdy = true;
      }
    }

    for (int i = 1; i < _n; ++i) {
      Node * nx = _nodes[i];
      for (int j = 1; j < _n; ++j) {
	Node & nxy = nx[j];
	if (nxy.bdy == false) {
	  nxy.V = 0;
	}
      }
    }
  }

public:
  int SOR(double a) { 
    if ((a <= 0.0) || (a >= 2.0*_halfedge)) {
      return 1; // fail
    } 
    set(a);
    const double tol = 1.0e-8;
    const double omega = 1.5;
    const int max_iter = 10000;
    for (int iter = 0; iter < max_iter; ++iter) {
      double norm = 0;
      // chessboard
      // sweep i+j = even
      for (int i = 1; i < _n; i++) {
	Node * nmid = _nodes[i];
	Node * nleft = _nodes[i-1];
	Node * nright = _nodes[i+1];
	for (int j = 1; j < _n; j++) {
	  if ((i+j)%2 == 1) continue; 
	  Node & n0 = nmid[j];
	  if (n0.bdy == true) continue;

	  Node & n1 = nright[j];
	  Node & n2 = nmid[j+1];
	  Node & n3 = nleft[j];
	  Node & n4 = nmid[j-1];
	  double delta_V = 0.25*(n1.V +n2.V +n3.V +n4.V) - n0.V;
	  if (norm < delta_V*delta_V) {
	    norm = delta_V*delta_V;
	  }
	  n0.V += omega*delta_V;
	}
      }
      // sweep i+j = odd
      for (int i = 1; i < _n; i++) {
	Node * nmid = _nodes[i];
	Node * nleft = _nodes[i-1];
	Node * nright = _nodes[i+1];
	for (int j = 1; j < _n; j++) {
	  if ((i+j)%2 == 0) continue; 
	  Node & n0 = nmid[j];
	  if (n0.bdy == true) continue;

	  Node & n1 = nright[j];
	  Node & n2 = nmid[j+1];
	  Node & n3 = nleft[j];
	  Node & n4 = nmid[j-1];
	  double delta_V = 0.25*(n1.V +n2.V +n3.V +n4.V) - n0.V;
	  if (norm < delta_V*delta_V) {
	    norm = delta_V*delta_V;
	  }
	  n0.V += omega*delta_V;
	}
      }

      //std::cerr << "iter, norm = " << iter << "   " << norm << std::endl;

      if (norm <= tol) break;
    }
    return 0; // success
  }

  void circle(int nc, std::vector<double> &V) const {
    const double pi = 4.0*atan2(1.0,1.0);
    const double r = _halfedge * 0.8;

    V.resize(nc, 0.0);    

    for (int ic = 0; ic < nc; ++ic) { 
      double theta = 2.0*pi*double(ic)/double(nc);
      double x0 = r*cos(theta);
      double y0 = r*sin(theta);
      int i0 = _n * (x0 / _halfedge + 1.0)/2.0;
      int j0 = _n * (y0 / _halfedge + 1.0)/2.0;
      bool interp = false;
      for (int i = i0-2; i <= i0+2; ++i) {
	double x1 = -_halfedge + 2.0*_halfedge*double(i)/double(_n);
	double x2 = x1 + 2.0*_halfedge/double(_n);
	if ((x1 > x0) || (x2 < x0)) continue;
	double lambda = (x0 - x1)/(x2 - x1);
	Node * nx1 = _nodes[i];
	Node * nx2 = _nodes[i+1];
	for (int j = j0-2; j <=j0+2; ++j) {
	  double y1 = -_halfedge + 2.0*_halfedge*double(j)/double(_n);
	  double y2 = y1 + 2.0*_halfedge/double(_n);
	  if ((y1 > y0) || (y2 < y0)) continue;
	  double mu = (y0 - y1)/(y2 - y1);
	  Node & nx1y1 = nx1[j];
	  Node & nx1y2 = nx1[j+1];
	  Node & nx2y1 = nx2[j];
	  Node & nx2y2 = nx2[j+1];
	  double V_interp = (1.0-lambda)*(1.0-mu)*nx1y1.V 
	                    +lambda*(1.0-mu)*nx2y1.V 
	                    +lambda*mu*nx2y2.V 
	                    +(1.0-lambda)*mu*nx1y2.V;
	  V[ic] = V_interp;
	  interp = true;
	  break;
	}
	if (interp == true) break;
      }
    }
  }

private:
  // data
  int _n;
  int _off;
  Node **_nodes;
  const double _halfedge;
};
