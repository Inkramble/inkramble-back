package inkramble.utils;

import inkramble.InkrambleBackApplication;
import inkramble.config.DevProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

@SpringBootTest(classes = InkrambleBackApplication.class)
class FileUtilsTest {

    @Autowired
    private DevProperties devProperties;

    @Test
    void testFileExtension(){
        String testFileName = "test.ink";
        Assertions.assertThat(FileUtils.getExtension(testFileName)).isEqualTo("ink");
    }

    @Test
    void testPathExtension(){
        Path testPath = Path.of(devProperties.getStorage().getRootPath(),"test.ink");
        Assertions.assertThat(FileUtils.getExtension(testPath)).isEqualTo("ink");

    }
}
