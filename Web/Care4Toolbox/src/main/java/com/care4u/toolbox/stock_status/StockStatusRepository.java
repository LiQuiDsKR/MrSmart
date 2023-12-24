package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockStatusRepository extends JpaRepository<StockStatus, Long>, StockStatusRepositoryCustom {
	
	StockStatus findByToolIdAndToolboxId(long toolId, long toolboxId);
	
	List<StockStatus> findAllByCurrentDay( LocalDate date);
	
	Page<StockStatus> findAllByToolboxIdAndCurrentDayBetween(long toolboxId, LocalDate startDate, LocalDate endDate, Pageable pageable);
	
	StockStatus findByToolIdAndToolboxIdAndCurrentDay(long toolId, long toolboxId, LocalDate date);
	
	@Query("SELECT s FROM StockStatus s WHERE s.toolbox.id = :toolboxId " +
	           "AND s.currentDay = :date " +
	           "AND s.tool.name LIKE %:toolName% " +
	           "And s.tool.subGroup.id IN :subGroupIds")
	    Page<StockStatus> findAllByToolboxIdAndCurrentDay(
	        @Param("toolboxId") Long toolboxId,
	        @Param("date") LocalDate date,
	        @Param("toolName") String toolName,
	        @Param("subGroupIds") List<Long> subGroupIds,
	        Pageable pageable
	    );
	
	@Query("SELECT s.currentDay FROM StockStatus s ORDER BY s.currentDay DESC")
	LocalDate getLatestCurrentDay();
}