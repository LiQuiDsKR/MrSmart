package com.care4u.toolbox.toolbox_tool_label;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.care4u.entity.BaseEntity;
import com.care4u.toolbox.Toolbox;
import com.care4u.toolbox.tool.Tool;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString

@Entity
@Table(name="toolbox_tool_label")
public class ToolboxToolLabel extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Toolbox toolbox;
	
	@NotNull
	private String location;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Tool tool;
	
	@NotNull
	private String qrcode;
	
	@Builder
	public ToolboxToolLabel(Toolbox toolbox, String location, Tool tool, String qrcode) {
		this.toolbox = toolbox;
		this.location = location;
		this.tool = tool;
		this.qrcode = qrcode;
	}
	
	public void update(Toolbox toolbox, String location, Tool tool, String qrcode) {
		this.toolbox = toolbox;
		this.location = location;
		this.tool = tool;
		this.qrcode = qrcode;
	}
}