package annotate4j.core.tests.validation;

import annotate4j.core.tests.utils.CauseException;
import annotate4j.core.validation.ValidatorImpl;
import annotate4j.core.validation.Validator;
import annotate4j.core.validation.exceptions.ValidationException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Savin
 */
public class ValidatorTest {

    @Test
    public void testValidator() throws Exception {
        List<Class> classes = getDataClasses("data");
        Validator v = new ValidatorImpl();
        for (Class clazz : classes) {
            CauseException cause = (CauseException) clazz.getAnnotation(CauseException.class);
            try {
                v.validate(clazz);
            } catch (ValidationException ve) {
                if (cause.exception().isInstance(ve)) {
                    System.out.println(ve.toString());
                } else {
                    throw new Exception("Catch an unexpected exception:  expected exception: " +
                            cause.exception().getCanonicalName() + ", provided exception: " +
                            ve.getClass().getCanonicalName() + ". Validated class: " + clazz.getCanonicalName());
                }
                continue;
            }
            throw new Exception("The ValidationException " + cause.exception().getCanonicalName() + " does not throws. Validated class: " +
                    clazz.getCanonicalName());
        }
    }

    private List<Class> getDataClasses(String dirName) throws Exception {
        List<Class> list = new ArrayList<Class>();
        URL dataUrl = this.getClass().getResource(dirName);
        String packageName = this.getClass().getPackage().getName() + "." + dirName.replaceAll("/", ".");

        File dir = new File(dataUrl.toURI());
        if (!dir.isDirectory()) {
            throw new IOException(dataUrl + " should be a directory");
        }
        for (File f : dir.listFiles()) {
            String fileName = f.getName();
            if (!f.isDirectory() && fileName.endsWith(".class")) {
                fileName = fileName.substring(0, fileName.length() - 6);
                Class c = Class.forName(packageName + "." + fileName);
                if (c.getAnnotation(CauseException.class) != null) {
                    list.add(c);
                }
            } else if (f.isDirectory()) {
                list.addAll(getDataClasses(dirName + "/" + f.getName()));
            }
        }
        return list;
    }
}
