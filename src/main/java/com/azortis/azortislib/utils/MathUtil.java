/*
 * MIT License
 *
 * Copyright (c) 2021 Azortis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.azortis.azortislib.utils;

/**
 * A class which contains mathematical utility methods.
 */
@SuppressWarnings("unused")
public class MathUtil {
    /**
     * Takes the original value given and the values already given and finds the closest value to the original.
     * Originally used to determine which cardinal direction a player is facing based off of degrees.
     *
     * @param original The value to compare against the other values
     * @param values   The values which should be considered to find the closest.
     * @return Whichever value is closest to the original value.
     */
    public static int roundToClosest(int original, int... values) {
        int closest = Integer.MAX_VALUE;
        int value = 0;
        for (int i : values) {
            int range = Math.abs(i - original);
            if (range < closest) {
                value = i;
                closest = range;
            }
        }
        return value;
    }
}
