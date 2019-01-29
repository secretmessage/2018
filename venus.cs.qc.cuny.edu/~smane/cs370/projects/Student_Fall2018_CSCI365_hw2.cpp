#include <iostream>
#include <cmath>
#include <vector>
using namespace std;

class Bond {
    public:
        Bond(double T, double F, double c=0, int freq=2) {
            if(F < 0)
                Face = 0;
            else
                Face = F;
            if(freq < 1)
                cpn_freq = 1;
            else 
                cpn_freq = freq;
            const double tol = 1e-6;
            num_coupons = int(cpn_freq*T + tol);
            if(num_coupons < 0)
                num_coupons = 0;
            T_maturity = num_coupons / cpn_freq;
            if(num_coupons > 0)
                coupons.resize(num_coupons, c);
        }
        
        ~Bond() {
            coupons.clear();
            num_coupons = 0;
        }
        
        // public methods
        int set_coupons(vector<double> & c) {
            if(c.size() < num_coupons)
                return 1;
            for(int i=0; i<c.size(); i++) {
                if(c[i] <= 0)
                    coupons[i] = 0;
                else 
                    coupons[i] = c[i];
            }
            return 0;
        }
        
        int FV_duration(double t0, double y, double &B, double &Macaulay_duration, double &modified_duration) const {
                if(num_coupons <= 0 || t0 >= T_maturity)
                    return 1;
                double y_percent = y * .01;
                for(int i=0; i<num_coupons-1; i++) {
                    double t_i = double(i+1)/double(cpn_freq);
                    if(t_i <= t0 + 1e-6)
                        continue;
                    double n = coupons[i] / cpn_freq;
                    n /= pow((1 + (y_percent/cpn_freq)), cpn_freq*(t_i - t0));
                    B += n;
                    n *= (i - t0 + 1);
                    Macaulay_duration += n;
                }
                double nf = Face + (coupons[num_coupons-1] / cpn_freq);
                nf /= pow((1 + (y_percent/cpn_freq)), cpn_freq*(T_maturity - t0));
                B += nf;
                nf *= (T_maturity - t0);
                Macaulay_duration += nf;
                Macaulay_duration /= B;
                modified_duration = Macaulay_duration / (1 + (y_percent * cpn_freq));
                return 0;
            }
        int yield(double B_target, double tol, int max_iter, double t0, double & y, int & num_iter) const {
            y = 0;
            num_iter = 0;
            if(B_target <= 0 || num_coupons <= 0 || t0 >= T_maturity)
                return 1;
            double y_low = 0;
            double y_high = 100;
            double B_y_low = FairValue(t0, y_low);
            double diff_B_y_low = B_y_low - B_target;
            if(fabs(diff_B_y_low) <= tol) {
                y = y_low;
                return 0;
            }
            double B_y_high = FairValue(t0, y_high);
            double diff_B_y_high = B_y_high - B_target;
            if(fabs(diff_B_y_high) <= tol) {
                y = y_high;
                return 0;
            }
            if(diff_B_y_low * diff_B_y_high > 0) {
                y = 0;
                return 1;
            }
            for(num_iter = 1; num_iter < max_iter; num_iter++) {
                y = (y_low + y_high)/2;
                double B = FairValue(t0, y);
                double diff_B = B - B_target;
                if(fabs(diff_B) <= tol) {
                    return 0;
                }
                if(diff_B * diff_B_y_low > 0)
                    y_low = y;
                else 
                    y_high = y;
                if(fabs(y_high - y_low) <= tol)
                    return 0;
            }
            y = 0; 
            return 1;
        }
        
        double FairValue(double t0, double y) const {
            double B = 0;
            double Macaulay_duration = 0;
            double modified_duration = 0;
            FV_duration(t0, y, B, Macaulay_duration, modified_duration);
            return B;
        }
        
        double maturity() const { return T_maturity; }
    private:
    // data
        double Face;
        double T_maturity;
        int cpn_freq;
        int num_coupons;
        vector<double> coupons;
};

int main() {
	
	Bond bond = Bond(2, 100, 4);
	double d;
	int i;
	cout << bond.yield(23, 1e-4, 1e8, 0, d, i) << endl;
	cout << d;
}