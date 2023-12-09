package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.OutstandingRentalSheetAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.OutstandingRentalSheetDto

class ManagerReturnFragment() : Fragment() {
    lateinit var searchTypeSpinner: Spinner
    private lateinit var recyclerView: RecyclerView

    val gson = Gson()
    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_return, container, false)

        searchTypeSpinner = view.findViewById(R.id.SearchTypeSpinner)

        recyclerView = view.findViewById(R.id.ManagerLobby_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val category1Data = arrayOf("리더명", "대여자명", "공기구명")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, category1Data)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchTypeSpinner.adapter = adapter1

        //val adapter = OutstandingRentalSheetAdapter(sharedViewModel.outstandingRentalSheetList)
        val adapter = OutstandingRentalSheetAdapter(getOutstandingRentalSheetList()) { outstandingRentalSheet ->
            val fragment = ManagerOutstandingDetailFragment(outstandingRentalSheet)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        return view
    }

    fun getOutstandingRentalSheetList(): List<OutstandingRentalSheetDto> {
        /*
        bluetoothManager.dataSend("REQUEST_RentalRequestSheetList")
        if (bluetoothManager.dataReceiveSingle().equals("Ready")) {
            val sendMessage =
                gson.toJson(RentalRequestSheetCall(SheetStatus.REQUEST, sharedViewModel.toolBoxId))
            bluetoothManager.dataSend(sendMessage)
        }*/

        //val rentalrequestSheetListPageString = bluetoothManager.dataReceive()
        val outstandingRentalSheetListPageString = "{\"content\":[{\"id\":54479,\"rentalSheetDto\":{\"id\":105371,\"workerDto\":{\"id\":19,\"name\":\"박철민\",\"code\":\"100587\",\"password\":\"2a10.zXQlil6LwZUD8yaosd05e9zIvj3uuzdVYrDyGQqkLP.qbqn2kSSu\",\"partDto\":{\"id\":4,\"name\":\"정비안전지원섹션\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"leaderDto\":{\"id\":6,\"name\":\"김상빈\",\"code\":\"100348\",\"password\":\"2a10Z47PPiPMswwRAhhhLw3Ds.BysJIgT8OXzdKk.LlAGOzNkcHT66QgS\",\"partDto\":{\"id\":3,\"name\":\"-\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"approverDto\":{\"id\":113,\"name\":\"강성곤\",\"code\":\"100001\",\"password\":\"2a10KdrNeUKKhlEVFeniCWOnnOzXiHUpNE4IRwWWvYbJcai57sUG15.OK\",\"partDto\":{\"id\":13,\"name\":\"정비2파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"toolboxDto\":{\"id\":5222,\"name\":\"선강정비1실\",\"managerDto\":{\"id\":113,\"name\":\"강성곤\",\"code\":\"100001\",\"password\":\"2a10KdrNeUKKhlEVFeniCWOnnOzXiHUpNE4IRwWWvYbJcai57sUG15.OK\",\"partDto\":{\"id\":13,\"name\":\"정비2파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"systemOperability\":false},\"eventTimestamp\":\"2023-12-07T23:16:23.202999\",\"toolList\":[{\"id\":105372,\"toolDto\":{\"id\":80,\"name\":\"전동체인블럭\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B200011\",\"buyCode\":null,\"engName\":\"ElectricChainHoist\",\"spec\":\"1000Kg(양정7M)\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":7,\"outstandingCount\":7,\"tags\":\"\"},{\"id\":105373,\"toolDto\":{\"id\":83,\"name\":\"체인블럭\",\"subGroupDto\":{\"id\":81,\"name\":\"건설공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B200014\",\"buyCode\":null,\"engName\":\"ChainBlock\",\"spec\":\"5Ton(대경)\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":8,\"outstandingCount\":8,\"tags\":\"\"},{\"id\":105374,\"toolDto\":{\"id\":85,\"name\":\"마그네트(인양자석)\",\"subGroupDto\":{\"id\":81,\"name\":\"건설공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B200016\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"SPM-50/490kg\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":5,\"outstandingCount\":5,\"tags\":\"\"}]},\"totalCount\":7,\"totalOutstandingCount\":7}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"unpaged\":false,\"paged\":true},\"last\":true,\"totalPages\":1,\"totalElements\":4,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":4,\"empty\":false}"
        Log.d("Debug", "JSON String: $outstandingRentalSheetListPageString")
        val pagedata: Page = gson.fromJson(outstandingRentalSheetListPageString, Page::class.java)
        val listOutstandingRentalSheetDto = object : TypeToken<List<OutstandingRentalSheetDto>>(){}.type
        Log.d("Debug", "TypeToken: $listOutstandingRentalSheetDto")
        val outstandingRentalSheetDtoList: List<OutstandingRentalSheetDto> = gson.fromJson(gson.toJson(pagedata.content), listOutstandingRentalSheetDto)
        Log.d("Debug", "OutstandingRentalSheetDto List: $outstandingRentalSheetDtoList")
        return outstandingRentalSheetDtoList
    }
}