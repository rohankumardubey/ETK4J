package com.wildbitsfoundry.etk4j.systems.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.wildbitsfoundry.etk4j.math.MathETK;
import com.wildbitsfoundry.etk4j.math.complex.Complex;
import com.wildbitsfoundry.etk4j.math.polynomials.Polynomial;
import com.wildbitsfoundry.etk4j.systems.TransferFunction;
import com.wildbitsfoundry.etk4j.systems.filters.AnalogFilter.LowPassPrototype;
import com.wildbitsfoundry.etk4j.util.NumArrays;
import com.wildbitsfoundry.etk4j.util.Tuples.Tuple2;

public enum ApproximationType {
	CHEBYSHEV {
		
		@Override
		double getExactOrderNeeded(double fp, double fs, double ap, double as) {
			double wp = 2 * Math.PI * fp;
			double ws = 2 * Math.PI * fs;
			double amax = Math.pow(10, ap * 0.1) - 1;
			double amin = Math.pow(10, as * 0.1) - 1;
			
			return MathETK.acosh(Math.sqrt(amin / amax)) / MathETK.acosh(ws / wp);
		}

		@Override
		LowPassPrototype buildLowPassPrototype(int n, double ap, double as) {
			double eps = Math.sqrt(Math.pow(10, ap * 0.1) - 1);

			double a = 1.0 / n * MathETK.asinh(1 / eps);
			double sinha = Math.sinh(a);
			double cosha = Math.cosh(a);

			final double pid = Math.PI / 180.0;
			Complex[] poles = new Complex[n];
			if (n % 2 == 0) {
				int i = 0;
				for (double k : NumArrays.linsteps(-n * 0.5 + 1.0, 1, n * 0.5)) {
					double phik = 180.0 * (k / n) - 90.0 / n;
					poles[i++] = new Complex(-sinha * Math.cos(phik * pid), cosha * Math.sin(phik * pid));
				}
			} else {
				int i = 0;
				for (double k : NumArrays.linsteps(-(n - 1) * 0.5, 1, (n - 1) * 0.5)) {
					double phik = 180.0 * (k / n);
					poles[i++] = new Complex(-sinha * Math.cos(phik * pid), cosha * Math.sin(phik * pid));
				}
			}

			double N = 1;
			for (int k = 0; k < (int) Math.ceil(n / 2.0); ++k) {
				if (poles[k].imag() != 0.0) {
					N *= poles[k].real() * poles[k].real() + poles[k].imag() * poles[k].imag();
				} else {
					N *= -poles[k].real();
				}
			}
			TransferFunction tf = new TransferFunction(N, poles);
			return new LowPassPrototype(eps, tf);
		}

		@Override
		double getBandPassAp(double ap, double as1, double as2) {
			return ap;
		}

		@Override
		double getBandPassBW(int n, double eps, double Q, double w0, double omega) {
			return Q / w0;
		}

		@Override
		double getBandStopAp(double amax, double amin) {
			return amax;
		}

		@Override
		double getBandStopBW(int n, double eps, double Q, double w0, double omegas) {
			return w0 / Q;
		}

		@Override
		double getLowPassScalingFrequency(int n, double eps, double wp, double ws) {
			return wp;
		}

		@Override
		double getLowPassAttenuation(double ap, double as) {
			return ap;
		}

		@Override
		double getHighPassAttenuation(double ap, double as) {
			return ap;
		}

		@Override
		double getHighPassGainFactor(int n, double eps, double wp, double ws) {
			return wp;
		}
	},
	
	BUTTERWORTH {
		@Override
		double getExactOrderNeeded(double fp, double fs, double ap, double as) {
			double wp = 2 * Math.PI * fp;
			double ws = 2 * Math.PI * fs;
			double amax = Math.pow(10, ap * 0.1) - 1;
			double amin = Math.pow(10, as * 0.1) - 1;
			
			return Math.log10(amin / amax) / (2 * Math.log10(ws / wp));
		}

		@Override
		LowPassPrototype buildLowPassPrototype(int n, double ap, double as) {
			double eps = Math.sqrt(Math.pow(10, ap * 0.1) - 1);

			final double pid = Math.PI / 180.0;
			final double nInv = 1.0 / n;
			Complex[] poles = new Complex[n];
			if (n % 2 == 0) {
				for(int k = (-n >> 1) + 1, i = 0; k <= n >> 1; ++k, ++i) {
					double phik = nInv * (180.0 * k - 90.0);
					poles[i] = new Complex(-Math.cos(phik * pid), Math.sin(phik * pid));				
				}
			} else {
				for(int k = -(n - 1) >> 1, i = 0; k <= (n - 1) >> 1; ++k, ++i) {
					double phik = nInv * 180.0 * k;
					poles[i] = new Complex(-Math.cos(phik * pid), Math.sin(phik * pid));				
				}
			}
			Complex kden = new Complex(1.0, 0.0);
			for(Complex pole : poles) {
				kden.multiplyEquals(pole.uminus());
			}
			double k = kden.real();
			TransferFunction tf = new TransferFunction(new Complex[0], poles);
			return new LowPassPrototype(eps, tf);
		}

		@Override
		double getBandPassAp(double ap, double as1, double as2) {
			return ap;
		}

		@Override
		double getBandPassBW(int n, double eps, double Q, double w0, double omega) {
			return Q / w0;
		}

		@Override
		double getBandStopAp(double amax, double amin) {
			return amax;
		}

		@Override
		double getBandStopBW(int n, double eps, double Q, double w0, double omegas) {
			return w0 / Q;
		}

		@Override
		double getLowPassScalingFrequency(int n, double eps, double wp, double ws) {
			return Math.pow(eps, -1.0 / n) * wp;
		}

		@Override
		double getLowPassAttenuation(double ap, double as) {
			return ap;
		}

		@Override
		double getHighPassAttenuation(double ap, double as) {
			return ap;
		}

		@Override
		double getHighPassGainFactor(int n, double eps, double wp, double ws) {
			return wp / Math.pow(eps, -1.0 / n);
		}
	},
	
	INVERSE_CHEBYSHEV
	{
		@Override
		double getExactOrderNeeded(double fp, double fs, double ap, double as) {
			double wp = 2 * Math.PI * fp;
			double ws = 2 * Math.PI * fs;
			double amax = Math.pow(10, ap * 0.1) - 1;
			double amin = Math.pow(10, as * 0.1) - 1;
			
			return MathETK.acosh(Math.sqrt(amin / amax)) / MathETK.acosh(ws / wp);
		}

		@Override
		LowPassPrototype buildLowPassPrototype(int n, double ap, double as) {
			double eps = 1.0 / Math.sqrt(Math.pow(10, ap * 0.1) - 1);

			double a = 1.0 / n * MathETK.asinh(1 / eps);
			double sinha = Math.sinh(a);
			double cosha = Math.cosh(a);

			Complex[] poles = new Complex[n];
			List<Complex> zeros = new ArrayList<>(n);
			if (n % 2 == 0) {
				for (int k = (-n >> 1) + 1, i = 0; k <= n >> 1; ++k, ++i) {
					double phik = 180.0 * (1.0 * k / n) - 90.0 / n;
					Tuple2<Complex, Optional<Complex>> pz = this.calcPZ(phik, cosha, sinha, k, n);
					poles[i] = pz.Item1;
					if(pz.Item2.isPresent()) {
						zeros.add(pz.Item2.get());
					}
				}
			} else {
				for (int k = -(n - 1) >> 1, i = 0; k <= (n - 1) >> 1; ++k, ++i) {
					double phik = 180.0 * (1.0 * k / n);
					Tuple2<Complex, Optional<Complex>> pz = this.calcPZ(phik, cosha, sinha, k, n);
					poles[i] = pz.Item1;
					if(pz.Item2.isPresent()) {
						zeros.add(pz.Item2.get());
					}
				}
			}
			
			double G = 1;
			for (int k = 0; k < (int) Math.ceil(n / 2.0); ++k) {
				if (poles[k].imag() != 0.0) {
					G *= poles[k].real() * poles[k].real() + poles[k].imag() * poles[k].imag();
				} else {
					G *= -poles[k].real();
				}
			}
			double Gp = 1.00;
			for(int k = 0; k <= (int) Math.ceil(zeros.size() >> 1); k = k + 2) {
				Gp *= zeros.get(k).imag();
			}
			Gp *= Gp;
			G /= Gp;
			
			Polynomial num = new Polynomial(zeros.toArray(new Complex[zeros.size()]));
			num.multiplyEquals(G);
			Polynomial den = new Polynomial(poles);
			TransferFunction tf = new TransferFunction(num, den);

			return new LowPassPrototype(eps, tf);
		}

		private Tuple2<Complex, Optional<Complex>> calcPZ(double phik, double cosha, double sinha, int k, int n) {
			final double pid = Math.PI / 180.0;
			Complex pole = new Complex(-sinha * Math.cos(phik * pid), cosha * Math.sin(phik * pid));
			pole.divideEquals(Math.pow(pole.abs(), 2));
			Complex zero = null;
			double phikz = Math.PI / n * (k + 0.5);
			double sign = k < 0 ? -1 : 1;
			if(phikz != 0.5 * Math.PI) {
				zero = new Complex(0.0, sign / Math.cos(phikz));						
			}
			return Tuple2.createTuple(pole, Optional.ofNullable(zero));
		}
		
		@Override
		double getBandPassAp(double ap, double as1, double as2) {
			return as2;
		}

		@Override
		double getBandPassBW(int n, double eps, double Q, double w0, double omega) {
			return Q / w0;
		}

		@Override
		double getBandStopAp(double amax, double amin) {
			return amin;
		}

		@Override
		double getBandStopBW(int n, double eps, double Q, double w0, double omegas) {
			return w0 / Q;
		}

		@Override
		double getLowPassScalingFrequency(int n, double eps, double wp, double ws) {
			return ws;
		}

		@Override
		double getLowPassAttenuation(double ap, double as) {
			return as;
		}

		@Override
		double getHighPassAttenuation(double ap, double as) {
			return as;
		}

		@Override
		double getHighPassGainFactor(int n, double eps, double wp, double ws) {
			return ws;
		}
	},
	ELLIPTIC {

		@Override
		double getExactOrderNeeded(double fp, double fs, double ap, double as) {
			// 	Digital Filter Designer's Handbook: With C++ Algorithms by C. Britton Rorabaugh
			double k = fp / fs;
			double kp = Math.sqrt(Math.sqrt(1 - k * k));
			double u = 0.5 * (1 - kp) / (1 + kp);
			double q = u + 2 * Math.pow(u, 5) + 15 * Math.pow(u, 9) + 150 * Math.pow(u, 13);
			double D = (Math.pow(10.0, 0.1 * as) - 1) / (Math.pow(10.0, 0.1 * ap) - 1);
			
			//  Alternative method using elliptic integrals
			//	double rt = fp / fs;
			//	double kn = Math.sqrt((Math.pow(10.0, 0.1 * ap) - 1) / (Math.pow(10.0, 0.1 * as) - 1));
			//	double rtp = Math.sqrt(1 - rt * rt);
			//	double knp = Math.sqrt(1 - kn * kn);
			//	return compEllipInt1(rt) * compEllipInt1(knp) / (compEllipInt1(rtp) * compEllipInt1(kn)); 

			
			return (Math.log10(16.0 * D) / Math.log10(1.0 / q));
		}

		@Override
		LowPassPrototype buildLowPassPrototype(int n, double ap, double as) {
			if(n == 1) {
				// filter becomes Chebyshev I
				Complex[] z = new Complex[0];
				Complex[] p = new Complex[1];
				p[0] = new Complex(-Math.sqrt(1.0 / (Math.pow(10.0, ap * 0.1) - 1.0)), 0.0);
				double k = -p[0].real();
				return null;
			}
			
			double dbn = Math.log(10.0) * 0.05;
			int n0 = (int) MathETK.rem(n, 2);
			int n3 = (n - n0) >> 1;
			double apn = dbn * ap;
			double asn = dbn * as;
			
			List<Double> e = new ArrayList<>();
			e.add(Math.sqrt(2.0 * Math.exp(apn) * Math.sinh(apn)));
			
			List<Double> g = new ArrayList<>();
			g.add(e.get(0) / Math.sqrt(Math.exp(2 * asn) - 1));
			
			double v = g.get(0);
			int m2 = 0;
			while(v > 1.0e-150) {
				v = (v / (1.0 + Math.sqrt(1 - v * v)));
				v *= v;
				++m2;
				g.add(v);
			}
			
			int m1 = 0;
			List<Double> ek = new ArrayList<>(m1);
			for(int i = 0; i < 10; ++i) {
				m1 = m2 + i;
				while(ek.size() <= m1) {
					ek.add(0.0);
				}
				ek.set(m1, 4.0 * Math.pow((g.get(m2) / 4.0), Math.pow(2.0, i) / n));
				if(ek.get(m1) < 1.0e-14) {
					break;
				}
			}
			
			for(int en = m1; en >= 1; --en) {
				ek.set(en - 1, 2.0 * Math.sqrt(ek.get(en)) / (1.0 +  ek.get(en)));
			}
			
			double a = 0.0;
			for(int en = 1; en <= m2; ++en) {
				a = (1.0 + g.get(en)) * e.get(en - 1) * 0.5;
				e.add(a + Math.sqrt(a * a + g.get(en)));
			}
			
			double u2 = Math.log((1 + Math.sqrt(1 + Math.pow(e.get(m2), 2))) / e.get(m2)) / n;
			Complex[] zeros = new Complex[n % 2 != 0 ? n - 1 : n];
			Complex[] poles = new Complex[n];
			Complex j = new Complex(0.0, 1.0);
			Complex mj = j.conj();
			for(int i = 0, m = zeros.length - 1; i < n3; ++i, m = m - 2) {
				double u1 = (2.0 * i + 1.0) * Math.PI / (2.0 * n);
				Complex c = mj.divide(new Complex(-u1, u2).cos());
				double d = 1.0 / Math.cos(u1);
				for(int en = m1; en >=1; --en) {
					double k = ek.get(en);
					c = c.subtract(c.invert().multiply(k));
					c.divideEquals(1 + k);
					d = (d + k / d) / (1 + k);
				}
				Complex pole = c.invert();
				poles[m] = pole;
				poles[m - 1] = pole.conj();
				Complex zero = new Complex(0.0, d / ek.get(0));
				zeros[m] = zero;
				zeros[m - 1] = zero.conj();
			}
			if(n0 == 1) {
				a = 1.0 / Math.sinh(u2);
				for(int en = m1; en >= 1; --en) {
					double k = ek.get(en);
					a = (a - k / a) / (1 + k);
				}
				poles[n - 1] = new Complex(-1.0 / a, 0.0);
			}
			// Compute gain k
			Complex knum = new Complex(1.0, 0.0);
			for(Complex zero : zeros) {
				knum.multiplyEquals(zero.uminus());
			}
			Complex kden = new Complex(1.0, 0.0);
			for(Complex pole : poles) {
				kden.multiplyEquals(pole.uminus());
			}
			kden.divideEquals(knum);
			double k = kden.real();
			if(n % 2 == 0) {
				double eps0 = e.get(0);
				k /= Math.sqrt(1 + eps0 * eps0); 
			}
			System.out.printf("z = %s%n", Arrays.toString(zeros));
			System.out.printf("p = %s%n", Arrays.toString(poles));
			System.out.printf("k = %.4g%n", k);
			return null;
		}

		@Override
		double getBandPassAp(double ap, double as1, double as2) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getBandPassBW(int n, double eps, double Q, double w0, double omega) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getBandStopAp(double amax, double amin) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getBandStopBW(int n, double eps, double Q, double w0, double omegas) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getLowPassScalingFrequency(int n, double eps, double wp, double ws) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getLowPassAttenuation(double ap, double as) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getHighPassAttenuation(double ap, double as) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		double getHighPassGainFactor(int n, double eps, double wp, double ws) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	};
	int getMinOrderNeeded(double fp, double fs, double ap, double as) {
		return (int) Math.ceil(this.getExactOrderNeeded(fp, fs, ap, as));
	}
	abstract double getExactOrderNeeded(double fp, double fs, double ap, double as);
	abstract LowPassPrototype buildLowPassPrototype(int n, double ap, double as);
	abstract double getBandPassAp(double ap, double as1, double as2);
	abstract double getBandPassBW(int n, double eps, double Q, double w0, double omega);
	abstract double getBandStopAp(double amax, double amin);
	abstract double getBandStopBW(int n, double eps, double Q, double w0, double omegas);
	abstract double getLowPassScalingFrequency(int n, double eps, double wp, double ws);
	abstract double getLowPassAttenuation(double ap, double as);
	abstract double getHighPassAttenuation(double ap, double as);
	abstract double getHighPassGainFactor(int n, double eps, double wp, double ws);
}
