package annotate4j.core.bin.exceptions;

import annotate4j.core.bin.annotation.ContainerSize;

/**
 * Throws if container (List or array) does not has ContainerSize annotation
 * It is necessary to determinate the Container size. Otherwise it is impossible to
 * read the container.
 *
 * @author Eugene Savin
 */
public class UnspecifiedContainerSizeException extends AnnotationNotSpecifiedException {


    public UnspecifiedContainerSizeException(String fieldName, String className) {
        super(ContainerSize.class.getName(), fieldName, className);
    }

}
