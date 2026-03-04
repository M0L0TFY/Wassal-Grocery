package com.Wassal.mapper;

import com.Wassal.dto.DetailedStoreResponse;
import com.Wassal.dto.StoreRequest;
import com.Wassal.dto.StoreResponse;
import com.Wassal.model.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface StoreMapper {
    //Map Store object to StoreResponseDTO
    StoreResponse toDTO(Store store);

    //Map Store object to DetailStoreResponseDTO
    DetailedStoreResponse toDetailedDTO(Store store);

    //Map StoreRequestDTO to Store object
    Store toEntity(StoreRequest storeRequest);

    //Updates an existing Store object via the new StoreRequestDTO (Hibernate detects the changes and updates it to database (JPA Dirty Checking))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void toUpdatedEntity(@MappingTarget Store store, StoreRequest request);
}
