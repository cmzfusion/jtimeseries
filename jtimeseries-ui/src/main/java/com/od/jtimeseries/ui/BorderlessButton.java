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
package com.od.jtimeseries.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 17:54:18
 */
public class BorderlessButton extends JButton {

    public BorderlessButton(ImageIcon i) {
        this(i, false);
    }

    public BorderlessButton(ImageIcon i, boolean borderPainted) {
        super(i);
        setBorderPainted(borderPainted);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setIconTextGap(0);
        setPreferredSize(new Dimension(i.getIconWidth(),i.getIconHeight()));
    }

}
