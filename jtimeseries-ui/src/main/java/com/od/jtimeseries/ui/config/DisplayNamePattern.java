/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.config;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-Jun-2009
 * Time: 10:32:46
 */
public class DisplayNamePattern {

    private String pattern;
    private String replacement;
    private transient boolean failed;

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

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    //nb. equals is on pattern only, so that we can check a rule for a pattern exists but not replace
    //it if the user has defined their own custom replacement
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayNamePattern that = (DisplayNamePattern) o;

        if (pattern != null ? !pattern.equals(that.pattern) : that.pattern != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pattern != null ? pattern.hashCode() : 0;
        result = 31 * result + (replacement != null ? replacement.hashCode() : 0);
        return result;
    }
}
