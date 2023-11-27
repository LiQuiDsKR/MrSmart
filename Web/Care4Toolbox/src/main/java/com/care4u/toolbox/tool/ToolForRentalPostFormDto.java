package com.care4u.toolbox.tool;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.stock_status.StockStatusDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ToolForRentalPostFormDto {
	
	int page=0;
	
	int size=10;
	
	String name;
	
	@NotNull
	Long toolboxId;
	
	@NotNull
	List<Long> subGroupId;

}
