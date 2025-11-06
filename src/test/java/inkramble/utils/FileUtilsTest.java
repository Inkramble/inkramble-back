package inkramble.utils;

import inkramble.TestConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;


class FileUtilsTest {

    @Test
    void testFileExtension(){
        String testFileName = "test.ink";
        Assertions.assertThat(FileUtils.getExtension(testFileName)).isEqualTo("ink");
    }

    @Test
    void testPathExtension(){
        Path testPath = Path.of(TestConstants.getRootPath(),"test.ink");
        Assertions.assertThat(FileUtils.getExtension(testPath)).isEqualTo("ink");

    }
}
