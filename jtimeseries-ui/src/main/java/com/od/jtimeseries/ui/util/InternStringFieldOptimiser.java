package com.od.jtimeseries.ui.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/03/11
 * Time: 19:15
 *
 * A utility to reduce the memory overhead of classes which have
 * private String fields, by using reflection to set the field value to an interned instance
 */
public class InternStringFieldOptimiser<E> {

    private List<Field> fieldList = new LinkedList<Field>();

    public InternStringFieldOptimiser(Class<E> clazz, String... fields) {
        for (String field : fields) {
            try {
                Field f = clazz.getDeclaredField(field);
                f.setAccessible(true);
                fieldList.add(f);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void optimise(E instanceToOptimize) {

        for (Field f : fieldList) {
            try {
                String fieldValue = (String)f.get(instanceToOptimize);

                if ( fieldValue != null) {
                    fieldValue = fieldValue.intern();
                    f.set(instanceToOptimize, fieldValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
