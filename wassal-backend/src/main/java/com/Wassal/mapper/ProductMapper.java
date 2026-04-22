package com.Wassal.mapper;

import com.Wassal.dto.ProductRequest;
import com.Wassal.dto.ProductResponse;
import com.Wassal.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS //Avoids calling setter on null values marked with @Builder.Default
)
public interface ProductMapper {
    ProductResponse toDTO(Product product);
    Product toEntity(ProductRequest productRequest);
    //Maps the old product to an updated Product object via the new ProductRequestDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void toUpdatedEntity(@MappingTarget Product product, ProductRequest request);
    List<ProductResponse> toDTO(List<Product> products);
}
