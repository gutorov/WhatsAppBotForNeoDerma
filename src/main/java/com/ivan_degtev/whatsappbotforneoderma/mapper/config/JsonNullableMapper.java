package com.ivan_degtev.whatsappbotforneoderma.mapper.config;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Позволяет MapStruct корректно обрабатывать объекты JsonNullable, предоставляя методы для упаковки
 * и распаковки значений,а также для проверки наличия значений.
 * Помогает управлять сериализацией и десериализацией полей, которые могут быть нулевыми,
 * сохраняя логику обработки null в одном месте.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class JsonNullableMapper {
    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
