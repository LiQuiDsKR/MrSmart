
import androidx.lifecycle.ViewModel
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.rental.RentalToolDto
import com.mrsmart.standard.tool.ToolDtoSQLite

class SharedViewModel : ViewModel() {
    var toolBoxId: Long = 5222

    var worker = MembershipSQLite(0,"","","","","","","", "" )
    var leader = MembershipSQLite(0,"","","","","","","", "" )
    lateinit var approver: MembershipSQLite

    var loginWorker = MembershipSQLite(0,"","","","","","","", "" )
    var loginManager = MembershipSQLite(0,"","","","","","","", "" )

    val rentalRequestToolList: MutableList<ToolDtoSQLite> = mutableListOf()
    val outstandingRentalSheetList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
    val outstandingRentalToolList: MutableList<RentalToolDto> = mutableListOf()

    /*
    fun ToolDto2RentalRequestToolFormDto (): List<RentalRequestToolFormDto> {
        val rentalRequestToolFormDto: MutableList<RentalRequestToolFormDto> = mutableListOf()

        for (tool: ToolDtoSQLite in toolList) {
            rentalRequestToolFormDto.add(RentalRequestToolFormDto(tool.id, 1))
        }

        return
    }
    */

}
