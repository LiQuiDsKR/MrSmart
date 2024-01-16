
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.ToolWithCount
import com.mrsmart.standard.tool.ToolboxCompressDto
import com.mrsmart.standard.tool.ToolboxDto

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222
    var toolBoxList: List<ToolboxCompressDto> = listOf()

    var worker = MembershipSQLite(0,"","","","","","","", "" )
    var leader = MembershipSQLite(0,"","","","","","","", "" )

    var loginWorker = MembershipSQLite(0,"","","","","","","", "" )
    var loginManager = MembershipSQLite(0,"","","","","","","", "" )

    val rentalRequestToolIdList: MutableList<Long> = mutableListOf()
    var toolWithCountList : MutableList<ToolWithCount> = mutableListOf()

    var qrScannerText: String = ""
}
