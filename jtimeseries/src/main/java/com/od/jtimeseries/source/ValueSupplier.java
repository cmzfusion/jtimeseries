package com.od.jtimeseries.source;

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 19:17:32
 *
 * A ValueSupplier some logic which can be executed to obtain a value
 * May be used by a TimedValueSource to obtain a value every time a scheduler triggers
 */
public interface ValueSupplier {

    /**
     * @return Numeric value, or Numeric.NaN if no value can be obtained
     */
     Numeric getValue();
}
