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
    private Float cycleTime;    
    private LocalDateTime productionDateTime;    
    private String shift;    
    private String sku;
    private Integer numberofProcess;    
    private Integer p1_beforeGlueStatus;    
    private Integer p1_afterGlueStatus;
    private Float p1_toxLoadMax;
    private Float p1_toxLoadMin;
    private Float p1_toxLoadActual;
    private Float p1_toxDisplacementMax;
    private Float p1_toxDisplacementMin;
    private Float p1_toxDisplacementActual;
    private Integer p1_graphStatus;
    private Integer p2_beforeGlueStatus;
    private Integer p2_afterGlueStatus;
    private Float p2_toxLoadMax;
    private Float p2_toxLoadMin;
    private Float p2_toxLoadActual;
    private Float p2_toxDisplacementMax;
    private Float p2_toxDisplacementMin;
    private Float p2_toxDisplacementActual;
    private Integer p2_graphStatus;
    private Integer finalStatus;    
    private Integer totalPartCount;    
    private Integer okCount;    
    private Integer notOkCount;    
    private String operatorName;

    // for getter and setter and constructor used lombok
}
