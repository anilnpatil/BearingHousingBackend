package TVS_SFL.BearingHousingBackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "bearing_housing_production_data", indexes = {@Index(name = "idx_barcode", columnList = "barcode")})
public class BearingHousingProductionData {

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
    
    // @Column(name = "p1_tox_load_max")
    // private Float p1_toxLoadMax;

    // @Column(name = "p1_tox_load_min")
    // private Float p1_toxLoadMin;

    @Column(name = "p1_tox_load_actual")
    private Float p1_toxLoadActual;
    
    @Column(name = "p1_tox_displacement_max")
    private Float p1_toxDisplacementMax;

    @Column(name = "p1_tox_displacement_min")
    private Float p1_toxDisplacementMin;

    @Column(name = "p1_tox_displacement_actual")
    private Float p1_toxDisplacementActual;

    @Column(name = "p1_graph_status")
    private Integer p1_graphStatus;

    @Column(name = "p2_before_glue_status")
    private Integer p2_beforeGlueStatus;

    @Column(name = "p2_after_glue_status")
    private Integer p2_afterGlueStatus;

    // @Column(name = "p2_tox_load_max")
    // private Float p2_toxLoadMax;

    // @Column(name = "p2_tox_load_min")
    // private Float p2_toxLoadMin;

    @Column(name = "p2_tox_load_actual")
    private Float p2_toxLoadActual;
    
    @Column(name = "p2_tox_displacement_max")
    private Float p2_toxDisplacementMax;

    @Column(name = "p2_tox_displacement_min")
    private Float p2_toxDisplacementMin;

    @Column(name = "p2_tox_displacement_actual")
    private Float p2_toxDisplacementActual;

    @Column(name = "p2_graph_status")
    private Integer p2_graphStatus;

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