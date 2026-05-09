package com.example.hometwin.repository;

import com.example.hometwin.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    
    Page<Item> findByRoomId(String roomId, Pageable pageable);
    
    Page<Item> findByPersonId(String personId, Pageable pageable);
    
    Page<Item> findByRoomIdAndPersonId(String roomId, String personId, Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE i.status = 'active' ORDER BY i.addedAt DESC")
    Page<Item> findActiveItems(Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE (i.name LIKE %:keyword% OR i.description LIKE %:keyword% OR i.location LIKE %:keyword%) AND (:roomId IS NULL OR i.roomId = :roomId) ORDER BY i.addedAt DESC")
    Page<Item> searchItems(@Param("keyword") String keyword, @Param("roomId") String roomId, Pageable pageable);
    
    long countByRoomId(String roomId);
    
    long countByPersonId(String personId);
    
    long countByStatus(String status);
    
    @Query("SELECT i.roomId, COUNT(i) FROM Item i WHERE i.status = 'active' GROUP BY i.roomId")
    List<Object[]> countActiveItemsByRoom();
    
    @Query("SELECT i.personId, COUNT(i) FROM Item i WHERE i.status = 'active' GROUP BY i.personId")
    List<Object[]> countActiveItemsByPerson();
}
