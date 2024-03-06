
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tool.ToolWithCount

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222

    var worker : MembershipDto? = null
    var leader : MembershipDto? = null

    var loginWorker: MembershipDto? = null
    var loginManager: MembershipDto? = null

    val rentalRequestToolIdList: MutableList<Long> = mutableListOf()
    var toolWithCountList : MutableList<ToolWithCount> = mutableListOf()

    var qrScannerText: String = ""
}
