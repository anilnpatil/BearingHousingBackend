package TVS_SFL.BearingHousingBackend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilePathConfigTest {

    @Test
    void normalizePathString_shouldStandardizeWindowsAndUncPaths() {
        assertEquals("D:/BearingHousingOldFiles", FilePathConfig.normalizePathString("D:\\BearingHousingOldFiles"));
        assertEquals("E:/BearingHousingImages/IV-4", FilePathConfig.normalizePathString("E:/BearingHousingImages/IV-4"));
        assertEquals("//server/share/Archive", FilePathConfig.normalizePathString("\\\\server\\share\\Archive"));
    }
}
