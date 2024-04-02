package com.mrsmart.standard.tag

import com.mrsmart.standard.tool.ToolDto
import com.mrsmart.standard.tool.group.MainGroupDto
import com.mrsmart.standard.tool.group.SubGroupDto
import com.mrsmart.standard.toolbox.ToolboxDto
import com.mrsmart.standard.toolbox.ToolboxService

data class ToolboxToolLabelSQLite(
    val id: Long,
    val toolboxId: Long,
    val location: String,
    val toolId: Long,
    val qrcode: String
)