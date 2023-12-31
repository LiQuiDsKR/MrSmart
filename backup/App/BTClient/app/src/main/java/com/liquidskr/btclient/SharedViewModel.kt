
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.ToolWithCount

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222 // 하드코딩

    var worker = MembershipSQLite(0,"","","","","","","", "" )
    var leader = MembershipSQLite(0,"","","","","","","", "" )

    var loginWorker = MembershipSQLite(0,"","","","","","","", "" )
    var loginManager = MembershipSQLite(0,"","","","","","","", "" )

    val rentalRequestToolIdList: MutableList<Long> = mutableListOf()
    var toolWithCountList : MutableList<ToolWithCount> = mutableListOf()

}
