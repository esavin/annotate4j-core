package annotate4j.core.bin.utils;

import annotate4j.core.bin.exceptions.ClassesNotInSameHierarchyException;

/**
 * Helper class to cast parent object to inheritor
 *
 * @author Eugene Savin
 */
public interface CastHelper {
    /**
     * Copy all fields value from parent object to inheritor
     *
     * @param parent    parent object
     * @param inheritor inherit object
     * @return inheritor type instance
     * @throws ClassesNotInSameHierarchyException
     *          if the objects not in same hierarchy
     */
    public Object cast(Object parent, Object inheritor) throws ClassesNotInSameHierarchyException;
}
