
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.ToolDtoSQLite

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222 // 하드코딩

    var worker = MembershipSQLite(0,"","","","","","","", "" )
    var leader = MembershipSQLite(0,"","","","","","","", "" )

    var loginWorker = MembershipSQLite(0,"","","","","","","", "" )
    var loginManager = MembershipSQLite(0,"","","","","","","", "" )

    val rentalRequestToolList: MutableList<ToolDtoSQLite> = mutableListOf()

}
