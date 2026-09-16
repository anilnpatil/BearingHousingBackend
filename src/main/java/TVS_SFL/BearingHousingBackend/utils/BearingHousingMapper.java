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
        dto.setP1_toxStartLoad(entity.getP1_toxStartLoad());
        dto.setP1_toxMidLoad(entity.getP1_toxMidLoad());
        dto.setP1_toxEndLoad(entity.getP1_toxEndLoad());
        dto.setP1_toxStartDisplacement(entity.getP1_toxStartDisplacement());
        dto.setP1_toxMidDisplacement(entity.getP1_toxMidDisplacement());
        dto.setP1_toxEndDisplacement(entity.getP1_toxEndDisplacement());
        dto.setP1_graphStatus(entity.getP1_graphStatus());
        dto.setP2_beforeGlueStatus(entity.getP2_beforeGlueStatus());
        dto.setP2_afterGlueStatus(entity.getP2_afterGlueStatus());
        dto.setP2_toxStartLoad(entity.getP2_toxStartLoad());
        dto.setP2_toxMidLoad(entity.getP2_toxMidLoad());
        dto.setP2_toxEndLoad(entity.getP2_toxEndLoad());
        dto.setP2_toxStartDisplacement(entity.getP2_toxStartDisplacement());
        dto.setP2_toxMidDisplacement(entity.getP2_toxMidDisplacement());
        dto.setP2_toxEndDisplacement(entity.getP2_toxEndDisplacement());
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
        entity.setP1_toxStartLoad(dto.getP1_toxStartLoad());
        entity.setP1_toxMidLoad(dto.getP1_toxMidLoad());
        entity.setP1_toxEndLoad(dto.getP1_toxEndLoad());
        entity.setP1_toxStartDisplacement(dto.getP1_toxStartDisplacement());
        entity.setP1_toxMidDisplacement(dto.getP1_toxMidDisplacement());
        entity.setP1_toxEndDisplacement(dto.getP1_toxEndDisplacement());
        entity.setP1_graphStatus(dto.getP1_graphStatus());
        entity.setP2_beforeGlueStatus(dto.getP2_beforeGlueStatus());
        entity.setP2_afterGlueStatus(dto.getP2_afterGlueStatus());        
        entity.setP2_toxStartLoad(dto.getP2_toxStartLoad());
        entity.setP2_toxMidLoad(dto.getP2_toxMidLoad());
        entity.setP2_toxEndLoad(dto.getP2_toxEndLoad());
        entity.setP2_toxStartDisplacement(dto.getP2_toxStartDisplacement());
        entity.setP2_toxMidDisplacement(dto.getP2_toxMidDisplacement());
        entity.setP2_toxEndDisplacement(dto.getP2_toxEndDisplacement());
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
