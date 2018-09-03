//
// Convex hull 
//
// Sateesh R. Mane, copyright 2018
//

#include <algorithm>
#include <vector>
#include <map>
#include <cmath>
#include <ctime>
#include <chrono>
#include <random>
using namespace std;

#include <GL/gl.h>
#include <GL/glu.h>
#include "freeglut.h"


class CHull {
public:
  static const double xmax;
  static double aspect;
  static int nscan;
  static vector<pair<double,double>> v;
  static vector<pair<double,double>> hull;

  static void reset(void);
  static void step_hull(void);

private:
  CHull();
};

const double CHull::xmax = 10;
double CHull::aspect = 1;
int CHull::nscan = 0;
vector<pair<double,double>> CHull::v;
vector<pair<double,double>> CHull::hull;

static bool vcomp(const std::pair<double,double> &a, const std::pair<double,double> &b)
{
  // compare cotangent a.x/a.y > b.x/b.y
  // so  a.x * b.y > b.x * a.y
  double diff = a.first*b.second - b.first*a.second;

  if (diff > 0.0) {
    return true;
  }
  else if (diff < 0.0) {
    return false;
  }
  return ((a.first*a.first + a.second*a.second) < (b.first*b.first + b.second*b.second));
}

void CHull::reset(void)
{
  std::default_random_engine generator;
  generator.seed( time(0) ); // set initial seed
  std::uniform_int_distribution<int> k_distribution(8, 20);
  std::uniform_real_distribution<double> r_distribution(-0.75*xmax, 0.75*xmax);

  int npt = k_distribution(generator);
  v.resize(npt);
  for (auto &i : v) {
    i.first = r_distribution(generator);
    i.second = r_distribution(generator);
  }

  // find index of lowest value of y (then lowest x if necessary)
  int imin = 0;
  double xmin = v[0].first;
  double ymin = v[0].second;
  for (int j = 1; j < v.size(); ++j) {
    if (ymin > v[j].second) {
      xmin = v[j].first;
      ymin = v[j].second;
      imin = j;
    }
    else if (ymin == v[j].second) {
      if (xmin > v[j].first) {
	xmin = v[j].first;
	ymin = v[j].second;
	imin = j;
      }
    }
  }

  // create vectors dv
  std::vector<pair<double,double>> dv;
  for (int iv = 0; iv < v.size(); ++iv) {
    std::pair<double,double> i;
    i.first = v[iv].first - xmin;
    i.second = v[iv].second - ymin;
    if (iv == imin) {
      i.first = 0;
      i.second = 0;
    }
    dv.push_back(i);
  }

  std::sort(dv.begin(), dv.end(), vcomp);

  // add back (xmin, ymin) to sorted vectors
  for (int ip = 0; ip < v.size(); ++ip) {
    v[ip].first = dv[ip].first + xmin;
    v[ip].second = dv[ip].second + ymin;
  }

  // clear the convex hull
  hull.clear();
  nscan = 0;
}

void CHull::step_hull(void)
{
  // initialize convex hull
  if (nscan <= 0) {
    hull.push_back(v[0]);
    hull.push_back(v[1]);
    nscan = 2;
    return;
  }

  // test if hull is complete
  if (nscan >= v.size()) 
    return;

  // scan a point, update hull 
  int i = nscan;
  int last = hull.size() - 1;
  int second_last = last - 1;
  double ux = hull[last].first - hull[second_last].first;
  double uy = hull[last].second - hull[second_last].second;
  double wx = v[i].first - hull[last].first;
  double wy = v[i].second - hull[last].second;

  const double tol = 1.0e-20;
  double cross_product = ux*wy - uy*wx;
  if (cross_product > tol) {
    hull.push_back(v[i]);
    ++nscan;
  }
  else if (std::abs(cross_product) <= tol) {
    // straight line = replace old point by new point
    hull.pop_back();
    hull.push_back(v[i]);
    ++nscan;
  }
  else {
    // clockwise rotation = erase a point
    hull.pop_back();
  }

  // close the convex hull
  if (nscan >= v.size()) { 
    hull.push_back(v[0]);
  }
}

// callbacks
static void Key_up(void)
{
  //scale *= 1.05;
}

static void Key_down(void)
{
  //scale /= 1.05;
}

static void Key_right(void)
{
  //scale = 1.0;
}

static void Key_left(void)
{
  //scale = 1.0;
}

static void Mouse_left(int state)
{
  if (state == GLUT_UP) {
    CHull::step_hull();
  }
}


// called when keyboard key is pressed
static void KeyboardFunc(unsigned char key, int x, int y)
{
  switch(key) {
  default:
    return;

  case 27:   // escape key
  case 'q':
  case 'Q':
    glutDestroyWindow( 1 );
    return;

  case 'r':
  case 'R':
  case '0':
    CHull::reset();
    break;
  }
  glutPostRedisplay();
}

// called when special keys are pressed
static void SpecialFunc(int key, int x, int y)
{
  switch(key) {
  default:
  case GLUT_KEY_LEFT:
    Key_left();
    break;
  case GLUT_KEY_RIGHT:
    Key_right();
    break;
  case GLUT_KEY_UP:
    Key_up();
    break;
  case GLUT_KEY_DOWN:
    Key_down();
    break;
  }
  glutPostRedisplay();
}

// called when a mouse button is clicked
static void MouseFunc(int button, int state, int x, int y)
{
  switch(button) {
  default:
    break;

  case GLUT_LEFT_BUTTON:
    Mouse_left(state);
    break;

  case GLUT_RIGHT_BUTTON:
    CHull::reset();
    break;
  }
  glutPostRedisplay();
}

static void Display()
{
  // clear the redering window
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  std::string s1 = "click/release mouse left button to display next step (ESC to exit)";
  glColor3d( 0.0, 1.0, 0.0 );
  glRasterPos2d(-0.8*CHull::xmax, 0.9*CHull::xmax/CHull::aspect);
  //void *font = GLUT_BITMAP_TIMES_ROMAN_24;
  void *font = GLUT_BITMAP_HELVETICA_18;
  for (auto i : s1) {
    glutBitmapCharacter(font, i);
  }

  std::string s2 = "click '0' (zero) or mouse right button to generate new set of points";
  glColor3d( 0.0, 1.0, 0.0 );
  glRasterPos2d(-0.8*CHull::xmax, 0.82*CHull::xmax/CHull::aspect);
  for (auto i : s2) {
    glutBitmapCharacter(font, i);
  }

  glLoadIdentity();
  glTranslated ( 0.0, 0.0, 0.0 );
  
  // draw the points
  GLUquadric *quadric = gluNewQuadric();
  glColor3d( 1.0, 0.0, 0.0 );
  const double radius_pt = 0.1;
  for (auto i : CHull::v) {
    glPushMatrix(); 
    glTranslated ( i.first, i.second/CHull::aspect, 0.0 );
    //glutSolidSphere( radius_pt, 100, 10 );
    gluDisk(quadric, 0.0, 0.1, 100, 1);
    glPopMatrix(); 
  }

  // draw the hull
  glLineWidth(1.0);
  glBegin(GL_LINE_STRIP);
  glColor3d(1.0, 1.0, 0.0);
  for (auto i : CHull::hull) {
    glVertex3d(i.first, i.second/CHull::aspect, 0.0);
  }
  glEnd();

  // draw the points in the hull
  if (CHull::nscan >= CHull::v.size()) {
    glColor3d( 0.0, 0.0, 1.0 );
    for (auto i : CHull::hull) {
      glPushMatrix(); 
      glTranslated ( i.first, i.second/CHull::aspect, 0.0 );
      //glutSolidSphere( radius_pt, 100, 10 );
      gluDisk(quadric, 0.0, 0.1, 100, 1);
      glPopMatrix(); 
    }
  }

  glFlush();
  glutSwapBuffers();
}

// initialize OpenGL
static void OpenGLInit()
{
  glShadeModel( GL_FLAT );
  glClearColor( 0.0, 0.0, 0.0, 0.0 );
  glClearDepth( 1.0 );
  glDepthFunc( GL_LEQUAL );
  glEnable( GL_DEPTH_TEST );
}

// called when the window is resized
static void ResizeWindow(int w, int h)
{
  if (h < 1) h = 1;
  if (w < 1) w = 1;
  glViewport( 0, 0, w, h );
  glMatrixMode( GL_PROJECTION );
  glLoadIdentity();
  //gluPerspective( 60.0, (double)w/(double)h, 1.0, 20.0 );
  double xmax = CHull::xmax;
  CHull::aspect = (double)w/(double)h;
  glOrtho(-xmax, xmax, -xmax/CHull::aspect, xmax/CHull::aspect, -10.0, 10.0);
  glMatrixMode( GL_MODELVIEW );
}


int main( int argc, char** argv )
{
  CHull::reset(); // initialize the points

  glutInit(&argc, argv);
  glutInitWindowSize(700,500);

  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB );

  glutCreateWindow("Convex Hull");

  OpenGLInit();

  // callback functions
  glutKeyboardFunc( KeyboardFunc ); 
  glutSpecialFunc( SpecialFunc ); 
  glutMouseFunc( MouseFunc ); 
  glutReshapeFunc( ResizeWindow );
  
  glutIdleFunc( Display );
  glutDisplayFunc( Display );

  glutMainLoop();

  return 0;
}
