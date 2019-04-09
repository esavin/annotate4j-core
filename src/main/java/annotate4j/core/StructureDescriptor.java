package annotate4j.core;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provide the description for user implemented structure
 *
 * @author Eugene Savin
 */
public interface StructureDescriptor {

    /**
     * @return the annotated class representing any data structure (for example: structure of .class file)
     */
    public Class getStructureClass();

    /**
     * @return the short structure description: one phrase description of structure (for example: "Java .class file structure")
     */
    public String getShortDescription();

    /**
     * Loader which will be used to load structure class. The implementation should return null if no specific loader needed.
     * In this case the standard loader will be used.
     *
     * @param inputStream the input stream with data to load
     * @return the custom loader to load structure
     */
    public Loader getLoader(InputStream inputStream);

    /**
     * DumpBuilder witch will be used to dump structure. The implementation should return null if no specific dump builder needed.
     * In this case the standard dump builder will be used.
     *
     * @param outputStream the output stream to data writing
     * @return the custom dump builder to write data.
     */
    public DumpBuilder getDumpBuilder(OutputStream outputStream);

}
