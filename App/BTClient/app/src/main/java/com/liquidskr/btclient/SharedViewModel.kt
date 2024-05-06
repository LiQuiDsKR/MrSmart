
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipDto
import com.mrsmart.standard.tool.ToolWithCount

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222

    var loginWorker: MembershipDto? = null
    var loginManager: MembershipDto? = null
}
