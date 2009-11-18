/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.displaypattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-Jun-2009
 * Time: 10:32:46
 */
public class DisplayNamePattern {

    private String pattern;
    private String replacement;

    public DisplayNamePattern() {
    }

    public DisplayNamePattern(String pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public DisplayNamePattern(DisplayNamePattern toClone) {
        this.pattern = toClone.getPattern();
        this.replacement = toClone.getReplacement();
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public boolean isValid() {
        return pattern != null && pattern.length() > 0 && replacement != null && replacement.length() > 0;
    }
}
