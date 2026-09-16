package TVS_SFL.BearingHousingBackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(
    name = "bearing_housing_production_data_archive",
    indexes = {
        @Index(name = "idx_barcode", columnList = "barcode"),
        @Index(name = "idx_production_date_time", columnList = "production_date_time"),
        @Index(name = "idx_shift", columnList = "shift"),
        @Index(name = "idx_sku", columnList = "sku"),       
        @Index(name = "idx_shift_production_date_time", columnList = "shift, production_date_time")
    }
)
public class BearingHousingProductionDataArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(name = "barcode", length = 30, unique = true, nullable = false)
    private String barcode;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "shift")
    private Integer shift;

    @Column(name = "sku")
    private String sku;

    @Column(name = "number_of_process") //it refers to the number of pressing
    private Integer numberofProcess;

    @Column(name = "cycle_start_time")
    private LocalTime cycleStartTime;            
    
    @Column(name = "p1_before_glue_status")
    private Integer p1_beforeGlueStatus;

    @Column(name = "p1_after_glue_status")
    private Integer p1_afterGlueStatus;
    
    @Column(name = "p1_tox_start_load")
    private Float p1_toxStartLoad;

    @Column(name = "p1_tox_mid_load")
    private Float p1_toxMidLoad;

    @Column(name = "p1_tox_end_load")
    private Float p1_toxEndLoad;
    
    @Column(name = "p1_tox_start_displacement")
    private Float p1_toxStartDisplacement;

    @Column(name = "p1_tox_mid_displacement")
    private Float p1_toxMidDisplacement;

    @Column(name = "p1_tox_end_displacement")
    private Float p1_toxEndDisplacement;

    @Column(name = "p1_graph_status")
    private Integer p1_graphStatus;

    @Column(name = "p2_before_glue_status")
    private Integer p2_beforeGlueStatus;

    @Column(name = "p2_after_glue_status")
    private Integer p2_afterGlueStatus;

    @Column(name = "p2_tox_start_load")
    private Float p2_toxStartLoad;

    @Column(name = "p2_tox_mid_load")
    private Float p2_toxMidLoad;

    @Column(name = "p2_tox_end_load")
    private Float p2_toxEndLoad;
    
    @Column(name = "p2_tox_start_displacement")
    private Float p2_toxStartDisplacement;

    @Column(name = "p2_tox_mid_displacement")
    private Float p2_toxMidDisplacement;

    @Column(name = "p2_tox_end_displacement")
    private Float p2_toxEndDisplacement;

    @Column(name = "p2_graph_status")
    private Integer p2_graphStatus;

    @Column(name = "cup_consumed")
    private Integer cupConsumed;

    @Column(name = "final_status")
    private Integer finalStatus;

    @Column(name = "ok_count")
    private Integer okCount;

    @Column(name = "not_ok_count")
    private Integer notOkCount;
    
    @Column(name = "total_part_count")
    private Integer totalPartCount;        

    @Column(name = "cycle_time")
    private Integer cycleTime;

    @Column(name = "production_date_time")
    private LocalDateTime productionDateTime;

    // Getters and Setters used lombok annotations @Getter and @Setter
    
}