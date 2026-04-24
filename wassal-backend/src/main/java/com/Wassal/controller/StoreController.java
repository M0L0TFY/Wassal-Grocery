package com.Wassal.controller;

import com.Wassal.dto.DetailedStoreResponse;
import com.Wassal.dto.StoreRequest;
import com.Wassal.dto.StoreResponse;
import com.Wassal.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "3. Grocery Store")
public class StoreController {
    private final StoreService storeService;

    @Operation(summary = "GetAllStores")
    @GetMapping
    public ResponseEntity<Page<StoreResponse>> getAllStores(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(storeService.getAllStores(pageable));
    }
    @Operation(summary = "GetStoreById")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreById(storeId));
    }
    @Operation(summary = "GetStoreByName", description = "Return store entity by its name")
    @GetMapping("/search") //api/v1/stores/search?storeName=
    public ResponseEntity<StoreResponse> getStoreByName(@RequestParam String storeName) {
        return ResponseEntity.ok(storeService.getStoreByName(storeName));
    }

    @Operation(summary = "CreateStore")
    @PostMapping
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(request));
    }

    @Operation(summary = "UpdateStore")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable Long storeId, @Valid @RequestBody StoreRequest request){
        return ResponseEntity.ok(storeService.updateStore(storeId, request));
    }

    @Operation(summary = "DeleteStore")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "GetStoreWithManager", description = "Return store entity by its id with the store manager")
    @GetMapping("/{storeId}/manager")
    public ResponseEntity<DetailedStoreResponse> getStoreWithManager(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreWithManager(storeId));
    }
    @Operation(summary = "AssignManagerToStore", description = "Assign a user manager to a store")
    @PutMapping("{storeId}/manager/{userId}")
    public ResponseEntity<DetailedStoreResponse> assignManagerToStore(@PathVariable Long storeId, @PathVariable Long userId) {
        return ResponseEntity.ok(storeService.assignManagerToStore(storeId, userId));
    }

}
