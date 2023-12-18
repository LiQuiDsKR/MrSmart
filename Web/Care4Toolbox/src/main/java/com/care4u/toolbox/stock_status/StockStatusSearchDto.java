package com.care4u.toolbox.stock_status;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.care4u.toolbox.group.sub_group.SubGroupDto;
import com.care4u.toolbox.toolbox_tool_label.ToolboxToolLabelDto;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StockStatusSearchDto {
	
	int page=0;
	
	int size=10;
	
	String name; //stock.tool.name : 검색용
	
	@NotNull
	Long toolboxId;
	
	@NotNull
	List<Long> subGroupId;

}
