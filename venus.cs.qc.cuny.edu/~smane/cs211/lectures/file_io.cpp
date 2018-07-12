// demo file I/O
// Sateesh R. Mane
// July 2018

#include <iostream>
#include <fstream>                  // header for file I/O
#include <string>
#include <vector>

using namespace std;

int main()
{
  vector<int> iv;
  vector<double> dv;
  vector<string> sv;
  string infile("infile.txt");      // input file name 
  string outfile("outfile.txt");    // output file name

  ifstream ifs(infile);             // ifstream object

  if (ifs.good() == false) {        // check if file opened successfully
    cout << "File not found: " << infile << endl;
    return 0;
  }

  while (true) {
    int a;
    double d;
    string s;
    ifs >> a >> d >> s;             // read from file
    if (ifs.eof() == true) break;   // end of file? break out of loop
    cout << a << "  " << d << "  " << s << endl;  // print for debugging
    iv.push_back(a);
    dv.push_back(d);
    sv.push_back(s);
  }

  ifs.close();  // optional, file will cloae automatically when ifs goes out of scope

  // find max int, min double, longest string
  int a_max = iv[0];
  double d_min = dv[0];
  string s_long = sv[0];
  for (int i = 0; i < iv.size(); ++i) {
    if (a_max < iv[i]) a_max = iv[i];
    if (d_min > dv[i]) d_min = dv[i];
    if (s_long.length() < sv[i].length()) s_long = sv[i];
  }

  ofstream ofs1(outfile, ios::out);  // "write mode": overwrites outfile
  ofs1 << a_max << endl;             
  ofs1 << d_min << endl;             // write to file
  ofs1 << s_long << endl;
  ofs1.close();                      // close the file

  ofstream ofs2(outfile, ios::app);  // "append mode": add to end of file

  ofs2 << endl;
  ofs2 << "append mode: add to end of file" << endl;
  ofs2 << a_max << "   " << d_min << "   " << s_long << endl;

  ofs2.close();                      // close the file

  return 0;
}
