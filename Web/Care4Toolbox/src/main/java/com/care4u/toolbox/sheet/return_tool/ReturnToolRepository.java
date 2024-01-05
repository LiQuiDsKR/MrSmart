package com.care4u.toolbox.sheet.return_tool;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnToolRepository extends JpaRepository<ReturnTool, Long> {
	
	List<ReturnTool> findAllByReturnSheetId(long returnSheetId);
	
	List<ReturnTool> findAllByRentalToolId(long rentalToolId);

<<<<<<< Updated upstream
	ReturnTool findByReturnSheetIdAndRentalToolId(long id, @NotNull Long rentalToolDtoId);
=======
	ReturnTool findByReturnSheetIdAndRentalToolId(long id, Long toolDtoId);
>>>>>>> Stashed changes
	
}