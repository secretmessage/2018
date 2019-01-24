#include <iostream>
#include <thread>

using namespace std;

class MyClass {
public:
  int sum;
  int len;

  void calc_sum() {
    sum = 0;
    for (int i = 1; i <= len; ++i) {
      sum += i;
    }
  }
};


void ThreadFunc(MyClass *mc, int n) 
{
  mc->len = n;
  mc->calc_sum();
}

int main()
{
  const int num_threads = 6;
  MyClass mc[num_threads];

  std::thread *thr[num_threads];   // array of pointers
  for (int i = 0; i < num_threads; ++i) {
    thr[i] = new std::thread(ThreadFunc, &mc[i], i+1);
  }

  std::cout << "all threads spawned" << std::endl;

  for (int i = 0; i < num_threads; ++i) {
    thr[i]->join();
  }

  for (int i = 0; i < num_threads; ++i) {
    int s = mc[i].sum;
    std::cout << "thread #, sum = " << (i+1) << "   " << s << std::endl;
  }

  for (int i = 0; i < num_threads; ++i) {
    delete thr[i];
  }

  std::cout << "end main" << std::endl;
  return 0;
}
