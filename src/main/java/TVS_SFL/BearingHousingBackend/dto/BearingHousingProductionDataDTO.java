package TVS_SFL.BearingHousingBackend.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BearingHousingProductionDataDTO {

    private Long id;    
    private String barcode;    
    private LocalTime cycleStartTime;    
    private LocalDateTime cycleEndTime;    
    private Integer cycleTime;    
    private LocalDateTime productionDateTime;    
    private String shift;    
    private String sku;
    private Integer numberofProcess;    
    private Integer p1_beforeGlueStatus;    
    private Integer p1_afterGlueStatus;
    private Float p1_toxStartLoad;
    private Float p1_toxMidLoad;
    private Float p1_toxEndLoad;
    private Float p1_toxStartDisplacement;
    private Float p1_toxMidDisplacement;
    private Float p1_toxEndDisplacement;
    private Integer p1_graphStatus;
    private Integer p2_beforeGlueStatus;
    private Integer p2_afterGlueStatus;
    private Float p2_toxStartLoad;
    private Float p2_toxMidLoad;
    private Float p2_toxEndLoad;
    private Float p2_toxStartDisplacement;
    private Float p2_toxMidDisplacement;
    private Float p2_toxEndDisplacement;
    private Integer p2_graphStatus;
    private Integer cupConsumed;
    private Integer finalStatus;    
    private Integer totalPartCount;    
    private Integer okCount;    
    private Integer notOkCount;    
    private String operatorName;

    // for getter and setter and constructor used lombok
}
