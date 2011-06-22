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
 * User: nick
 * Date: 26-Feb-2010
 * Time: 23:39:08
 *
 */
public enum ChartRangeMode {
    
    SingleRange("Single Range"),
    RangePerId("Range Per Id"),
    RangePerSeries("Range Per Series");

    private String description;

    private ChartRangeMode(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

}
