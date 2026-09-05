package TVS_SFL.BearingHousingBackend.services.impl;

import TVS_SFL.BearingHousingBackend.dto.SkuOption;
import TVS_SFL.BearingHousingBackend.exceptions.BadRequestException;
import TVS_SFL.BearingHousingBackend.exceptions.ResourceNotFoundException;
import TVS_SFL.BearingHousingBackend.services.SkuOptionService;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuOptionServiceImpl implements SkuOptionService {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS sku_options (
                id BIGSERIAL PRIMARY KEY,
                value VARCHAR(120) NOT NULL UNIQUE
            )
            """;

    private static final String[] DEFAULT_VALUES = {
            "6527865 LEGENDT", "6428633 SABRE", "5552916 LEXINGTON",
            "5494279 COYOTE 2", "5500145 COYOTE 1", "5601651 COLORADO",
            "6709348 BARRACUDA TP", "6709314 BARRACUDA SD", "6579004 KINETIC",
            "5601328 NTV", "6436259 TITANIUM", "6423094/X MERIDIAN"
    };

    private final JdbcTemplate jdbcTemplate;

    public SkuOptionServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void initializeTable() {
        jdbcTemplate.execute(CREATE_TABLE);
        for (String value : DEFAULT_VALUES) {
            jdbcTemplate.update("INSERT INTO sku_options (value) VALUES (?) ON CONFLICT (value) DO NOTHING", value);
        }
    }

    @Override
    public List<SkuOption> getAll() {
        return jdbcTemplate.query(
                "SELECT id, value FROM sku_options ORDER BY id",
                (rs, rowNum) -> new SkuOption(rs.getLong("id"), rs.getString("value")));
    }

    @Override
    public SkuOption add(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty() || normalized.length() > 120) {
            throw new BadRequestException("SKU value is required and must be 120 characters or fewer");
        }
        try {
            Long id = jdbcTemplate.queryForObject(
                    "INSERT INTO sku_options (value) VALUES (?) RETURNING id", Long.class, normalized);
            return new SkuOption(id, normalized);
        } catch (org.springframework.dao.DuplicateKeyException ex) {
            throw new BadRequestException("SKU already exists: " + normalized);
        }
    }

    @Override
    public void delete(Long id) {
        int deleted = jdbcTemplate.update("DELETE FROM sku_options WHERE id = ?", id);
        if (deleted == 0) {
            throw new ResourceNotFoundException("SKU not found: " + id);
        }
    }
}