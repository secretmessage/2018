//
// Sun, Jupiter and Comet : animation and tracking, get the comet to hit Jupiter
//
// Sateesh R. Mane, copyright 2018
//

#include <string>
#include <vector>
#include <cmath>
#include <ctime>

#include <GL/gl.h>
#include <GL/glu.h>
#include "freeglut.h"


// globals ... should be encapsulated in a class
static const double radius_Sun = 0.3;
static const double radius_Jupiter = 0.1;
static const double radius_comet = 0.05;

static const double M_Sun = 1.1;
static const double M_Jupiter = 0.5;

static bool animation = true;
static bool launchComet = false;

static bool first_call = true;
static double prev_time = 0;
static double current_time = 0;

static double scale = 1.0;

static double x_Jupiter0 = 0.0;
static double y_Jupiter0 = -1.3;
static double px_Jupiter0 = 1.0;
static double py_Jupiter0 = 0.0;

static double x_comet0 = 3.0;
static double y_comet0 = -3.0;
static double px_comet0 = 0;
static double py_comet0 = 0.3;

static double x_Jupiter = x_Jupiter0;
static double y_Jupiter = y_Jupiter0;
static double px_Jupiter = px_Jupiter0;
static double py_Jupiter = py_Jupiter0;

static double x_comet = x_comet0;
static double y_comet = y_comet0;
static double px_comet = px_comet0;
static double py_comet = py_comet0;

// callbacks
static void reset(void)
{
  animation = true;
  launchComet = false;
  
  first_call = true;
  prev_time = 0;
  current_time = 0;
  
  scale = 1.0;
  
  x_Jupiter = x_Jupiter0;
  y_Jupiter = y_Jupiter0;
  px_Jupiter = px_Jupiter0;
  py_Jupiter = py_Jupiter0;
  
  x_comet = x_comet0;
  y_comet = y_comet0;
  px_comet = px_comet0;
  py_comet = py_comet0;
}

static void Key_up(void)
{
  scale *= 1.05;
}

static void Key_down(void)
{
  scale /= 1.05;
}

static void Key_right(void)
{
  scale = 1.0;
}

static void Key_left(void)
{
  scale = 1.0;
}

static void Mouse_left(int state)
{
  if (state == GLUT_UP) {
    launchComet = true;
  }
  else if (state == GLUT_DOWN) {
    if (true == launchComet) {
      animation = !animation;
    }
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
    reset();
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
  }
  glutPostRedisplay();
}

// Runge-Kutta numerical integration
static int f(int n, double x, const std::vector<double> & y, std::vector<double> & g)
{
  const double &x_J  = y[0];
  const double &y_J  = y[1];
  const double &px_J = y[2];
  const double &py_J = y[3];
  const double &x_c  = y[4];
  const double &y_c  = y[5];
  const double &px_c = y[6];
  const double &py_c = y[7];

  double rsq_Jupiter = x_J*x_J + y_J*y_J;
  double tmp_Jupiter = M_Sun / pow(rsq_Jupiter, 1.5);
  g[0] = px_J;
  g[1] = py_J;  
  g[2] = -tmp_Jupiter*x_J;
  g[3] = -tmp_Jupiter*y_J;

  g[4] = 0;
  g[5] = 0;  
  g[6] = 0;
  g[7] = 0;

  if (launchComet) {
    double rsq_comet1 = x_c*x_c + y_c*y_c;
    double tmp_comet1 = M_Sun / pow(rsq_comet1, 1.5);
    double rsq_comet2 = (x_c-x_J)*(x_c-x_J) + (y_c-y_J)*(y_c-y_J);
    double tmp_comet2 = M_Jupiter / pow(rsq_comet2, 1.5);      

    if (rsq_comet1 < radius_Sun*radius_Sun) {
      animation = false;
      return 0;
    }
    if (rsq_comet2 < radius_Jupiter*radius_Jupiter) {
      animation = false;
      return 0;
    }

    g[4] = px_c;
    g[5] = py_c;  
    g[6] = -tmp_comet1*x_c -tmp_comet2*(x_c-x_J);
    g[7] = -tmp_comet1*y_c -tmp_comet2*(y_c-y_J);
  }

  return 0;
}

static int RK4(int n, double x, double h, 
	       std::vector<double> & y_in, 
	       std::vector<double> & y_out)
{
  int i = 0;
  int rc = 0;
  y_out = y_in;
  if ((n < 1) || (y_in.size() < n) || (y_out.size() < n)) return 1; // fail

  std::vector<double> g1(n, 0.0);
  std::vector<double> g2(n, 0.0);
  std::vector<double> g3(n, 0.0);
  std::vector<double> g4(n, 0.0);
  std::vector<double> y_tmp(n, 0.0); // temporary storage

  rc = f(n, x, y_in, g1);
  if (rc) return rc;
  for (i = 0; i < n; ++i) {
    y_tmp[i] = y_in[i] + 0.5*h*g1[i];
  }
  rc = f(n, x+0.5*h, y_tmp, g2);
  if (rc) return rc;
  for (i = 0; i < n; ++i) {
    y_tmp[i] = y_in[i] + 0.5*h*g2[i];
  }
  rc = f(n, x+0.5*h, y_tmp, g3);
  if (rc) return rc;
  for (i = 0; i < n; ++i) {
    y_tmp[i] = y_in[i] + h*g3[i];
  }
  rc = f(n, x+h, y_tmp, g4);
  if (rc) return rc;
  for (i = 0; i < n; ++i) {
    y_out[i] = y_in[i] + (h/6.0)*(g1[i] + 2.0*g2[i] + 2.0*g3[i] + g4[i]);
  }
  return 0;
}

static int Integration(const double &current_time, const double &prev_time)
{
  // Jupiter
  const int n = 8;
  std::vector<double> y_in(n, 0.0);
  std::vector<double> y_out(n, 0.0);

  const double dt = current_time - prev_time;

  y_in[0] = x_Jupiter;
  y_in[1] = y_Jupiter;
  y_in[2] = px_Jupiter;
  y_in[3] = py_Jupiter;
  y_in[4] = x_comet;
  y_in[5] = y_comet;
  y_in[6] = px_comet;
  y_in[7] = py_comet;

  int rc = RK4(n, current_time, dt, y_in, y_out);

  x_Jupiter  = y_out[0];
  y_Jupiter  = y_out[1];
  px_Jupiter = y_out[2];
  py_Jupiter = y_out[3];

  if (launchComet) {
    x_comet  = y_out[4];
    y_comet  = y_out[5];
    px_comet = y_out[6];
    py_comet = y_out[7];
  }

  //if ((x_Jupiter < 0.0) && (!launchComet)) {
  //  launchComet = true;
  //} 

  return rc;
}


static void Motion()
{
  current_time = 0.001*clock();
  if (first_call) {
    first_call = false;
    prev_time = current_time;
  }
  if (!animation) {
    prev_time = current_time;
  }

  // clear the redering window
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  if (!launchComet) {
    //const char *str = "click and release mouse to launch the comet";
    //const int len = strlen(str);
    std::string str = "click and release mouse to launch the comet";
    const int len = str.length();
    glColor3d( 0.0, 1.0, 0.0 );
    glRasterPos2d(-3.0, 2.0);
    void *font = GLUT_BITMAP_TIMES_ROMAN_24;
    for (int i = 0; i < len; ++i) {
      glutBitmapCharacter(font, str[i]);
    }
  }

  if (animation) {
    Integration(current_time, prev_time);
    prev_time = current_time;
  }
  
  glLoadIdentity();
  glTranslated ( 0.0, 0.0, 0.0 );
  
  // draw the sun
  glColor3d( 1.0, 1.0, 0.0 );
  glutSolidSphere( scale*radius_Sun, 100, 10 );
  
  // draw the planet
  glPushMatrix(); 
  glTranslated ( scale*x_Jupiter, scale*y_Jupiter, 0.0 );
  glColor3d( 1.0, 0.0, 0.0 );
  glutSolidSphere( scale*radius_Jupiter, 100, 10 );
  glPopMatrix(); 
  
  // draw the comet
  glPushMatrix(); 
  glTranslated ( scale*x_comet, scale*y_comet, 0.0 );
  glColor3d( 0.0, 0.7, 0.0 );
  glutSolidSphere( scale*radius_comet, 100, 10 );
  glPopMatrix(); 
  
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
  double xmax = 5;
  double aspect = (double)w/(double)h;
  glOrtho(-xmax, xmax, -xmax/aspect, xmax/aspect, -10.0, 10.0);
  glMatrixMode( GL_MODELVIEW );
}


int main( int argc, char** argv )
{
  glutInit(&argc, argv);
  glutInitWindowSize(700,500);

  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB );

  glutCreateWindow("Sun and Jupiter and Comet");

  OpenGLInit();

  // callback functions
  glutKeyboardFunc( KeyboardFunc ); 
  glutSpecialFunc( SpecialFunc ); 
  glutMouseFunc( MouseFunc ); 
  glutReshapeFunc( ResizeWindow );
  
  glutIdleFunc( Motion );
  glutDisplayFunc( Motion );

  glutMainLoop();

  return 0;
}
