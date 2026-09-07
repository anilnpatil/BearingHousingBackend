package TVS_SFL.BearingHousingBackend.utils;

import TVS_SFL.BearingHousingBackend.dto.BearingHousingProductionDataDTO;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;

public class BearingHousingMapper {

    /**
     * Convert Entity to DTO
     */
    public static BearingHousingProductionDataDTO entityToDTO(BearingHousingProductionData entity) {
        if (entity == null) {
            return null;
        }

        BearingHousingProductionDataDTO dto = new BearingHousingProductionDataDTO();
        dto.setId(entity.getId());
        dto.setBarcode(entity.getBarcode());
        dto.setCycleStartTime(entity.getCycleStartTime());
        // dto.setCycleEndTime(entity.getCycleEndTime());
        dto.setCycleTime(entity.getCycleTime());
        dto.setSku(entity.getSku());
        dto.setNumberofProcess(entity.getNumberofProcess());
        dto.setP1_beforeGlueStatus(entity.getP1_beforeGlueStatus());
        dto.setP1_afterGlueStatus(entity.getP1_afterGlueStatus());
        // dto.setP1_toxLoadMax(entity.getP1_toxLoadMax());
        // dto.setP1_toxLoadMin(entity.getP1_toxLoadMin());
        dto.setP1_toxLoadActual(entity.getP1_toxLoadActual());
        dto.setP1_toxDisplacementMax(entity.getP1_toxDisplacementMax());
        dto.setP1_toxDisplacementMin(entity.getP1_toxDisplacementMin());
        dto.setP1_toxDisplacementActual(entity.getP1_toxDisplacementActual());
        dto.setP1_graphStatus(entity.getP1_graphStatus());
        dto.setP2_beforeGlueStatus(entity.getP2_beforeGlueStatus());
        dto.setP2_afterGlueStatus(entity.getP2_afterGlueStatus());
        // dto.setP2_toxLoadMax(entity.getP2_toxLoadMax());
        // dto.setP2_toxLoadMin(entity.getP2_toxLoadMin());
        dto.setP2_toxLoadActual(entity.getP2_toxLoadActual());
        dto.setP2_toxDisplacementMax(entity.getP2_toxDisplacementMax());
        dto.setP2_toxDisplacementMin(entity.getP2_toxDisplacementMin());
        dto.setP2_toxDisplacementActual(entity.getP2_toxDisplacementActual());
        dto.setP2_graphStatus(entity.getP2_graphStatus());
        dto.setCupConsumed(entity.getCupConsumed());
        dto.setFinalStatus(entity.getFinalStatus());
        dto.setTotalPartCount(entity.getTotalPartCount());
        dto.setOkCount(entity.getOkCount());
        dto.setNotOkCount(entity.getNotOkCount());
        dto.setOperatorName(entity.getOperatorName());

        return dto;
    }

    /**
     * Convert DTO to Entity
     */
    public static BearingHousingProductionData dtoToEntity(BearingHousingProductionDataDTO dto) {
        if (dto == null) {
            return null;
        }

        BearingHousingProductionData entity = new BearingHousingProductionData();
        entity.setId(dto.getId());
        entity.setBarcode(dto.getBarcode());
        entity.setCycleStartTime(dto.getCycleStartTime());
        // entity.setCycleEndTime(dto.getCycleEndTime());
        entity.setCycleTime(dto.getCycleTime());
        entity.setSku(dto.getSku());
        entity.setNumberofProcess(dto.getNumberofProcess());
        entity.setP1_beforeGlueStatus(dto.getP1_beforeGlueStatus());
        entity.setP1_afterGlueStatus(dto.getP1_afterGlueStatus());
        // entity.setP1_toxLoadMax(dto.getP1_toxLoadMax());
        // entity.setP1_toxLoadMin(dto.getP1_toxLoadMin());
        entity.setP1_toxLoadActual(dto.getP1_toxLoadActual());
        entity.setP1_toxDisplacementMax(dto.getP1_toxDisplacementMax());
        entity.setP1_toxDisplacementMin(dto.getP1_toxDisplacementMin());
        entity.setP1_toxDisplacementActual(dto.getP1_toxDisplacementActual());
        entity.setP1_graphStatus(dto.getP1_graphStatus());
        entity.setP2_beforeGlueStatus(dto.getP2_beforeGlueStatus());
        entity.setP2_afterGlueStatus(dto.getP2_afterGlueStatus());
        // entity.setP2_toxLoadMax(dto.getP2_toxLoadMax());
        // entity.setP2_toxLoadMin(dto.getP2_toxLoadMin());
        entity.setP2_toxLoadActual(dto.getP2_toxLoadActual());
        entity.setP2_toxDisplacementMax(dto.getP2_toxDisplacementMax());
        entity.setP2_toxDisplacementMin(dto.getP2_toxDisplacementMin());
        entity.setP2_toxDisplacementActual(dto.getP2_toxDisplacementActual());
        entity.setP2_graphStatus(dto.getP2_graphStatus());
        entity.setCupConsumed(dto.getCupConsumed());
        entity.setFinalStatus(dto.getFinalStatus());
        entity.setTotalPartCount(dto.getTotalPartCount());
        entity.setOkCount(dto.getOkCount());
        entity.setNotOkCount(dto.getNotOkCount());
        entity.setOperatorName(dto.getOperatorName());

        return entity;
    }
}
