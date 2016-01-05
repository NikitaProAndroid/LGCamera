package com.lge.olaworks.datastruct;

public class Ola_ExifGpsUrational {
    public int degree_denominator;
    public int degree_numerator;
    public int min_denominator;
    public int min_numerator;
    public int sec_denominator;
    public int sec_numerator;

    public Ola_ExifGpsUrational(int degree_numerator, int degree_denominator, int min_numerator, int min_denominator, int sec_numerator, int sec_denominator) {
        this.degree_numerator = degree_numerator;
        this.degree_denominator = degree_denominator;
        this.min_numerator = min_numerator;
        this.min_denominator = min_denominator;
        this.sec_numerator = sec_numerator;
        this.sec_denominator = sec_denominator;
    }

    public Ola_ExifGpsUrational() {
        this.degree_numerator = 0;
        this.degree_denominator = 0;
        this.min_numerator = 0;
        this.min_denominator = 0;
        this.sec_numerator = 0;
        this.sec_denominator = 0;
    }
}
