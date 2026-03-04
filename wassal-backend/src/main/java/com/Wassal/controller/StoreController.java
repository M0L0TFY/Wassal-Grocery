package com.Wassal.controller;

import com.Wassal.dto.DetailedStoreResponse;
import com.Wassal.dto.StoreRequest;
import com.Wassal.dto.StoreResponse;
import com.Wassal.service.StoreService;
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
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Page<StoreResponse>> getAllStores(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(storeService.getAllStores(pageable));
    }
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreById(storeId));
    }
    @GetMapping("/search") //api/v1/stores/search?storeName=
    public ResponseEntity<StoreResponse> getStoreByName(@RequestParam String storeName) {
        return ResponseEntity.ok(storeService.getStoreByName(storeName));
    }

    @PostMapping
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(request));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable Long storeId, @Valid @RequestBody StoreRequest request){
        return ResponseEntity.ok(storeService.updateStore(storeId, request));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storeId}/manager")
    public ResponseEntity<DetailedStoreResponse> getStoreWithManager(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreWithManager(storeId));
    }
    @PutMapping("{storeId}/manager/{userId}")
    public ResponseEntity<DetailedStoreResponse> assignManagerToStore(@PathVariable Long storeId, @PathVariable Long userId) {
        return ResponseEntity.ok(storeService.assignManagerToStore(storeId, userId));
    }

}
