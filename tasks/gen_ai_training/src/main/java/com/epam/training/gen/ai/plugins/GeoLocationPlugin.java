package com.epam.training.gen.ai.plugins;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.util.Random;

public class GeoLocationPlugin {

    private final Random random = new Random();

    @DefineKernelFunction(name = "getCurrentLatitude", description = "Generates a random latitude between -90 and 90 degrees.")
    public double getRandomLatitude(
            @KernelFunctionParameter(
            name = "precision",
            description = "Optional number of decimal places to round to.",
            defaultValue = "6")
            int precision) {
        double latitude = -90 + (180 * random.nextDouble());
        return round(latitude, precision);
    }

    @DefineKernelFunction(name = "getCurrentLongitude", description = "Generates a random longitude between -180 to 180 degrees.")
    public double getRandomLongitude(
            @KernelFunctionParameter(
                    name = "precision",
                    description = "Optional number of decimal places to round to.",
                    defaultValue = "6")
            int precision) {
        double longitude = -180 + (360 * random.nextDouble());
        return round(longitude, precision);
    }

    private double round(double value, int precision) {
        double scale = Math.pow(10, precision);
        return Math.round(value * scale) / scale;
    }
}
