package inkramble.config;

import inkramble.InkrambleBackApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = InkrambleBackApplication.class)
public class PropertiesTest {

    @Autowired
    private DevProperties devProperties;

    @Test
    public void testDevProperties() {
        Assertions.assertThat(devProperties.getStorage().getRootPath())
                .isNotBlank();
        System.out.println(devProperties.getStorage().getRootPath());
    }
}
