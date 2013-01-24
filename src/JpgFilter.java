import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author seb
 * @version 1.0
 */
public class JpgFilter implements FileFilter {

    /**
     * Filfilter's behaviour : selects the jpg files.
     * 
     * @param file file to be tested
     * @return result of th test
     */
    @Override
    public boolean accept(File file) {
        String name = file.getName();
        return (name.matches(".*\\.jpg$"));
    }

}
