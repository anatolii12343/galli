package com.example.magic.models;

public class BounceInterpolator implements android.view.animation.Interpolator {
    private double mAmplitude;
    private double mFrequency;

    public BounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}