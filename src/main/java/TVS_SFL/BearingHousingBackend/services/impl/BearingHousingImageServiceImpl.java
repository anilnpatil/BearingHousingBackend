package TVS_SFL.BearingHousingBackend.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import TVS_SFL.BearingHousingBackend.dto.BearingHousingDataToImageResponse;
import TVS_SFL.BearingHousingBackend.dto.BearingHousingImageInfo;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingImageRepository;
import TVS_SFL.BearingHousingBackend.services.BearingHousingImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BearingHousingImageServiceImpl
        implements BearingHousingImageService {

    private final BearingHousingImageRepository repository;

    private static final String IMAGE_BASE_URL = "/api/bearing-housing/image";
    private static final String PDF_BASE_URL = "/api/bearing-housing/pdf";

    @Override
    public BearingHousingDataToImageResponse getData(String barcode) {

        BearingHousingProductionData data =
                repository.findByBarcode(barcode);

        if (data == null) {
            throw new RuntimeException("Barcode not found : " + barcode);
        }

        BearingHousingDataToImageResponse response =
                new BearingHousingDataToImageResponse();

        response.setProductionData(data);

        // P1 Images
        response.setP1_beforeImage(
                buildImageInfo(
                        data,
                        "P1",
                        data.getP1_beforeGlueStatus() == 1 ? "BOK" : "BNG"));

        response.setP1_afterImage(
                buildImageInfo(
                        data,
                        "P1",
                        data.getP1_afterGlueStatus() == 1 ? "AOK" : "ANG"));

        response.setP1_graphImage(
                buildPdfInfo(
                        data,
                        "P1"));

        // P2 Images (if exists)
        if (data.getNumberofProcess() != null
                && data.getNumberofProcess() >= 2) {

            response.setP2_beforeImage(
                    buildImageInfo(
                            data,
                            "P2",
                            data.getP2_beforeGlueStatus() == 1 ? "BOK" : "BNG"));

            response.setP2_afterImage(
                    buildImageInfo(
                            data,
                            "P2",
                            data.getP2_afterGlueStatus() == 1 ? "AOK" : "ANG"));

            response.setP2_graphImage(
                    buildPdfInfo(
                            data,
                            "P2"));
        }

        return response;
    }

    /**
     * Creates Image URL (JPEG)
     */
    private BearingHousingImageInfo buildImageInfo(
            BearingHousingProductionData data,
            String process,
            String statusCode) {

        String fileName =
                buildImageFileName(
                        data,
                        process,
                        statusCode);

        return new BearingHousingImageInfo(
                statusCode,
                IMAGE_BASE_URL + "/" + fileName);
    }

    /**
     * Creates PDF URL
     */
    private BearingHousingImageInfo buildPdfInfo(
            BearingHousingProductionData data,
            String process) {

        String fileName =
                buildPdfFileName(
                        data,
                        process);

        String status = "NOT_OK";

        if ("P1".equals(process) && data.getP1_graphStatus() != null && data.getP1_graphStatus() == 1) {
            status = "OK";
        }
        
        if ("P2".equals(process) && data.getP2_graphStatus() != null && data.getP2_graphStatus() == 1) {
            status = "OK";
        }

        return new BearingHousingImageInfo(
                status,
                PDF_BASE_URL + "/" + fileName);
    }

    /**
     * Builds Image filename
     * Example: Barcode_P1_BK_S1-280726.jpeg
     */
    private String buildImageFileName(
            BearingHousingProductionData data,
            String process,
            String status) {

        LocalDateTime dt =
                data.getProductionDateTime();

        String shift =
                "S" + data.getShift();

        String date =
                String.format(
                        "%02d%02d%02d",
                        dt.getDayOfMonth(),
                        dt.getMonthValue(),
                        dt.getYear() % 100);

        return data.getBarcode()
                + "_"
                + process
                + "_"
                + status
                + "_"
                + shift
                + "_"
                + date
                + ".jpeg";
    }

    /**
     * Builds PDF filename
     * Example: Barcode_P1_S1-280726.pdf
     */
    private String buildPdfFileName(
            BearingHousingProductionData data,
            String process) {

        LocalDateTime dt =
                data.getProductionDateTime();

        String shift =
                "S" + data.getShift();

        String date =
                String.format(
                        "%02d%02d%02d",
                        dt.getDayOfMonth(),
                        dt.getMonthValue(),
                        dt.getYear() % 100);

        return data.getBarcode()
                + "_"
                + process
                + "_"
                + shift
                + "_"
                + date
                + ".pdf";
    }
}