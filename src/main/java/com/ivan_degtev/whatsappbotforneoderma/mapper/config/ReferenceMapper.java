package com.ivan_degtev.whatsappbotforneoderma.mapper.config;

import com.ivan_degtev.whatsappbotforneoderma.model.interfaces.BaseEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Преобразование id в сущности и обратно - упрощает получение объектов из базы данных на основе их id.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public class ReferenceMapper {
    @Autowired
    private EntityManager entityManager; //интерфейс для взаимодействия в БД в JPA

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}