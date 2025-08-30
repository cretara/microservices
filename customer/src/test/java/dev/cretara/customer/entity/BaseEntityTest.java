package dev.cretara.customer.entity;

import dev.cretara.customer.model.BaseEntity;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

class BaseEntityTest {

    private static final Settings stringNullableSettings = Settings.create()
            .set(Keys.STRING_NULLABLE, true);

    private static final Model<BaseEntity> BASE_ENTITY_MODEL = Instancio.of(BaseEntity.class)
            .generate(field(BaseEntity::getCreatedBy), gen -> gen.string().length(5, 15))
            .generate(field(BaseEntity::getUpdatedBy), gen -> gen.string().length(5, 15))
            .generate(field(BaseEntity::getCreatedAt), gen -> gen.temporal().localDateTime().past())
            .generate(field(BaseEntity::getUpdatedAt), gen -> gen.temporal().localDateTime().past())
            .withSettings(stringNullableSettings)
            .toModel();

    @Test
    void shouldCreateBaseEntity_withRandomValues() {
        BaseEntity baseEntity = Instancio.of(BaseEntity.class)
                .generate(field(BaseEntity::getCreatedAt), generators -> generators.temporal().localDateTime())
                .generate(field(BaseEntity::getCreatedBy), generators -> generators.string().length(5, 20))
                .generate(field(BaseEntity::getUpdatedAt), generators -> generators.temporal().localDateTime())
                .generate(field(BaseEntity::getUpdatedBy), generators -> generators.string().length(5, 20))
                .create();

        assertThat(baseEntity.getCreatedAt()).isNotNull();
        assertThat(baseEntity.getCreatedBy()).isNotNull().hasSizeBetween(5, 20);
        assertThat(baseEntity.getUpdatedAt()).isNotNull();
        assertThat(baseEntity.getUpdatedBy()).isNotNull().hasSizeBetween(5, 20);
    }

    @Test
    void shouldCreateMultipleBaseEntities_withDifferentRandomValues() {
        BaseEntity entity1 = Instancio.create(BaseEntity.class);
        BaseEntity entity2 = Instancio.create(BaseEntity.class);

        assertThat(entity1.getCreatedBy()).isNotEqualTo(entity2.getCreatedBy());
        assertThat(entity1.getUpdatedBy()).isNotEqualTo(entity2.getUpdatedBy());
        assertThat(entity1.getCreatedAt()).isNotEqualTo(entity2.getCreatedAt());
        assertThat(entity1.getUpdatedAt()).isNotEqualTo(entity2.getUpdatedAt());

    }

    @Test
    void shouldCreateBaseEntity_withLimitedDateTimeRange() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        BaseEntity baseEntity = Instancio.of(BaseEntity.class)
                .generate(field(BaseEntity::getCreatedAt),
                        generators -> generators.temporal().localDateTime().range(startDate, endDate))
                .generate(field(BaseEntity::getCreatedBy),
                        generators -> generators.string().length(5, 20))
                .generate(field(BaseEntity::getUpdatedAt),
                        generators -> generators.temporal().localDateTime().range(startDate, endDate))
                .generate(field(BaseEntity::getUpdatedBy),
                        generators -> generators.string().length(5, 20))
                .create();

        assertThat(baseEntity.getCreatedAt())
                .isNotNull()
                .isAfterOrEqualTo(startDate)
                .isBeforeOrEqualTo(endDate);

        assertThat(baseEntity.getUpdatedAt())
                .isNotNull()
                .isAfterOrEqualTo(startDate)
                .isBeforeOrEqualTo(endDate);

        assertThat(baseEntity.getCreatedBy()).isNotNull().hasSizeBetween(5, 20);
        assertThat(baseEntity.getUpdatedBy()).isNotNull().hasSizeBetween(5, 20);
    }

    @Test
    void shouldApplyCallbacks_withCreatedAtAnd() {
        LocalDateTime baseTime = LocalDateTime.now().minusDays(1);

        BaseEntity baseEntity = Instancio.of(BaseEntity.class)
                .supply(field(BaseEntity::getUpdatedAt), random ->
                        baseTime.minusHours(random.intRange(1, 24)))
                .supply(field(BaseEntity::getCreatedAt), random ->
                        baseTime.minusHours(random.intRange(25, 48)))
                .create();

        assertThat(baseEntity.getCreatedAt()).isBefore(baseEntity.getUpdatedAt());
    }

    @Test
    void shouldHandleNullableFields_withNull30PercentChance() {
        BaseEntity baseEntity = Instancio.of(BaseEntity.class)
                .withSettings(stringNullableSettings)
                .generate(field(BaseEntity::getCreatedBy), gen -> gen.string().nullable()) // 30% chance of null
                .set(field(BaseEntity::getUpdatedBy), null)
                .create();

        assertThat(baseEntity.getUpdatedBy()).isNull();
    }

    @Test
    void shouldMapSubtypes() {
        List<BaseEntity> entities = Instancio.ofList(BaseEntity.class)
                .size(5)
                .subtype(all(BaseEntity.class), BaseEntity.class)
                .create();

        assertThat(entities).hasSize(5).allSatisfy(entity -> assertThat(entity).isNotNull());
    }

    @Test
    void shouldUseConditionalGeneration() {
        BaseEntity baseEntity = Instancio.of(BaseEntity.class)
                .generate(field(BaseEntity::getCreatedBy), gen -> gen.string().length(10))
                .filter(field(BaseEntity::getCreatedBy), (String value) -> value.startsWith("A"))
                .generate(field(BaseEntity::getUpdatedBy), gen -> gen.string().prefix("UPDATED_"))
                .create();

        assertThat(baseEntity.getCreatedBy()).startsWith("A");
        assertThat(baseEntity.getUpdatedBy()).startsWith("UPDATED_");
    }

    @Test
    void shouldUseReusableModel() {
        BaseEntity baseEntity = Instancio.create(BASE_ENTITY_MODEL);

        assertThat(baseEntity.getCreatedAt()).isBefore(LocalDateTime.now());
    }

}
