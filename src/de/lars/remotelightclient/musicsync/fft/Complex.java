package de.lars.remotelightclient.musicsync.fft;

public class Complex {
	private final double re;
	private final double im;

	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	public double abs() {
		return Math.hypot(this.re, this.im);
	}

	public Complex plus(Complex c) {
		return new Complex(this.re + c.re, this.im + c.im);
	}

	public Complex minus(Complex c) {
		return new Complex(this.re - c.re, this.im - c.im);
	}

	public Complex times(Complex c) {
		return new Complex(this.re * c.re - this.im * c.im, this.re * c.im + this.im * c.re);
	}
}
