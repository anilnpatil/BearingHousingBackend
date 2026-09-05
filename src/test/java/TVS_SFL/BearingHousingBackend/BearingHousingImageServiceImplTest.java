package TVS_SFL.BearingHousingBackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import TVS_SFL.BearingHousingBackend.services.impl.BearingHousingImageServiceImpl;

class BearingHousingImageServiceImplTest {

    @Test
    void shouldGenerateDuplicateExtensionCandidatesForStoredFiles() {
        String requested = "P06423094S260630002VSFLAD_P1_S1_A_OK_180826.jpeg";

        List<String> candidates = BearingHousingImageServiceImpl.getLookupCandidates(requested);

        assertEquals(2, candidates.size());
        assertTrue(candidates.contains(requested));
        assertTrue(candidates.contains("P06423094S260630002VSFLAD_P1_S1_A_OK_180826.jpeg.jpeg"));
    }

    @Test
    void shouldIncludeLegacyTwoDigitYearCandidate() {
        String requested = "P06436259S262046387VSFLAD_P1_S1_A_NG_05092026.jpeg";

        List<String> candidates = BearingHousingImageServiceImpl.getLookupCandidates(requested);

        assertTrue(candidates.contains("P06436259S262046387VSFLAD_P1_S1_A_NG_050926.jpeg"));
    }
}
