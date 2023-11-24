package com.liquidskr.fragment

import SharedViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.BluetoothManager
import com.liquidskr.btclient.LobbyActivity
import com.liquidskr.btclient.R
import com.liquidskr.btclient.RentalRequestSheetAdapter
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.page.Page
import com.mrsmart.standard.rental.RentalRequestSheetDto

class ManagerLobbyFragment(manager: Membership) : Fragment() {
    lateinit var rentalBtn: ImageButton
    lateinit var returnBtn: ImageButton
    lateinit var standbyBtn: ImageButton
    lateinit var recyclerView: RecyclerView
    val manager = manager;
    val gson= Gson()
    lateinit var bluetoothManager: BluetoothManager

    lateinit var welcomeMessage: TextView

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_lobby, container, false)
        welcomeMessage = view.findViewById(R.id.WelcomeMessage)
        rentalBtn = view.findViewById(R.id.RentalBtn)
        returnBtn = view.findViewById(R.id.ReturnBtn)
        standbyBtn = view.findViewById(R.id.StandbyBtn)
        bluetoothManager = BluetoothManager(requireContext(), requireActivity())

        recyclerView = view.findViewById(R.id.ManagerLobby_RecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        val adapter = RentalRequestSheetAdapter(getRentalRequestSheetList())
        recyclerView.adapter = adapter

        welcomeMessage.text = manager.name + "님 환영합니다."


        rentalBtn.setOnClickListener {
            // LobbyActivity에서 만든 managerRentalFragment를 가져와서 사용
            val lobbyActivity = activity as? LobbyActivity
            val managerRentalFragment = lobbyActivity?.managerRentalFragment

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            managerRentalFragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .addToBackStack(null)
                    .commit()
            }
        }

        returnBtn.setOnClickListener {
            // LobbyActivity에서 만든 managerRentalFragment를 가져와서 사용
            val lobbyActivity = activity as? LobbyActivity
            val managerReturnFragment = lobbyActivity?.managerReturnFragment

            // managerRentalFragment가 null이 아니라면 프래그먼트 교체
            managerReturnFragment?.let {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }

    fun getRentalRequestSheetList(): List<RentalRequestSheetDto> {
        /*
        bluetoothManager.dataSend("REQUEST_RentalRequestSheetList")
        if (bluetoothManager.dataReceiveSingle().equals("Ready")) {
            val sendMessage =
                gson.toJson(RentalRequestSheetCall(SheetStatus.REQUEST, sharedViewModel.toolBoxId))
            bluetoothManager.dataSend(sendMessage)
        }*/

        //val rentalrequestSheetListPageString = bluetoothManager.dataReceive()
        val rentalrequestSheetListPageString = "{\"content\":[{\"id\":1416,\"workerDto\":{\"id\":19,\"name\":\"박철민\",\"code\":\"100587\",\"password\":\"123\",\"partDto\":{\"id\":4,\"name\":\"정비안전지원섹션\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"leaderDto\":{\"id\":6,\"name\":\"김상빈\",\"code\":\"100348\",\"password\":\"123\",\"partDto\":{\"id\":3,\"name\":\"-\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"toolboxDto\":{\"id\":5222,\"name\":\"선강정비1실\",\"managerDto\":{\"id\":113,\"name\":\"강성곤\",\"code\":\"100001\",\"password\":\"123\",\"partDto\":{\"id\":13,\"name\":\"정비2파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"systemOperability\":false},\"status\":\"REQUEST\",\"eventTimestamp\":\"2023-11-17T13:44:41.888304\",\"toolList\":[{\"id\":1417,\"toolDto\":{\"id\":72,\"name\":\"에어임팩트렌치\",\"subGroupDto\":{\"id\":71,\"name\":\"에어공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"A330005\",\"buyCode\":null,\"engName\":\"AirImpactWrench\",\"spec\":\"\\\"IR21453/4\\\"\\\"\\\"\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":4,\"tags\":\"\"},{\"id\":1418,\"toolDto\":{\"id\":74,\"name\":\"스트레이트그라인더\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"A500048\",\"buyCode\":null,\"engName\":\"PortableGrinder\",\"spec\":\"GGS6S\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":1419,\"toolDto\":{\"id\":76,\"name\":\"휴대용용접기\",\"subGroupDto\":{\"id\":75,\"name\":\"용접기\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B120027\",\"buyCode\":null,\"engName\":\"InverterDcArcWldingMachine\",\"spec\":\"\\\"웰드월200LD2\",\"unit\":\"홀더10M\",\"price\":0,\"replacementCycle\":0},\"count\":5,\"tags\":\"\"},{\"id\":1420,\"toolDto\":{\"id\":80,\"name\":\"전동체인블럭\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B200011\",\"buyCode\":null,\"engName\":\"ElectricChainHoist\",\"spec\":\"1000Kg(양정7M)\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":2,\"tags\":\"\"}]},{\"id\":1426,\"workerDto\":{\"id\":13,\"name\":\"황선혜\",\"code\":\"800092\",\"password\":\"123\",\"partDto\":{\"id\":4,\"name\":\"정비안전지원섹션\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"leaderDto\":{\"id\":98,\"name\":\"김일영\",\"code\":\"100028\",\"password\":\"123\",\"partDto\":{\"id\":12,\"name\":\"정비1파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"toolboxDto\":{\"id\":5222,\"name\":\"선강정비1실\",\"managerDto\":{\"id\":113,\"name\":\"강성곤\",\"code\":\"100001\",\"password\":\"123\",\"partDto\":{\"id\":13,\"name\":\"정비2파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"systemOperability\":false},\"status\":\"REQUEST\",\"eventTimestamp\":\"2023-11-17T18:03:38.819921\",\"toolList\":[{\"id\":1427,\"toolDto\":{\"id\":115,\"name\":\"유압잭램(쇼트램)\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400013\",\"buyCode\":null,\"engName\":\"HydraulicJackRam\",\"spec\":\"TSSC50TonS50mmH109mm\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":11,\"tags\":\"\"},{\"id\":1428,\"toolDto\":{\"id\":116,\"name\":\"유압토크렌치소켓\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400016\",\"buyCode\":null,\"engName\":\"HydraulicTorqueWrenchSocket\",\"spec\":\"80mm\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":1429,\"toolDto\":{\"id\":117,\"name\":\"유압토크렌치소켓\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400018\",\"buyCode\":null,\"engName\":\"HydraulicTorqueWrenchSocket\",\"spec\":\"\\\"11/2\\\"\\\"90mm\\\"\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":4,\"tags\":\"\"},{\"id\":1430,\"toolDto\":{\"id\":122,\"name\":\"유압토크렌치소켓\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400023\",\"buyCode\":null,\"engName\":\"HydraulicTorqueWrenchSocket\",\"spec\":\"\\\"2\\\"\\\"115mm\\\"\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":3,\"tags\":\"\"},{\"id\":1431,\"toolDto\":{\"id\":124,\"name\":\"유압파이프벤더\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400026\",\"buyCode\":null,\"engName\":\"HydraulicPipeBender\",\"spec\":\"1/2'~2'\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":1432,\"toolDto\":{\"id\":126,\"name\":\"유압알미늄잭램\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400028\",\"buyCode\":null,\"engName\":\"HydraulicAluminumJackRam\",\"spec\":\"TAR20Ton150mmH250mm\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":7,\"tags\":\"\"},{\"id\":1433,\"toolDto\":{\"id\":130,\"name\":\"유압잭램\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400042\",\"buyCode\":null,\"engName\":\"HydraulicJackRam\",\"spec\":\"TS20TonS200mmH315mm\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":3,\"tags\":\"\"},{\"id\":1434,\"toolDto\":{\"id\":148,\"name\":\"전동해머드릴\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B500033\",\"buyCode\":null,\"engName\":\"ElectricHammerDrill\",\"spec\":\"PHD40\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":5,\"tags\":\"\"},{\"id\":1435,\"toolDto\":{\"id\":144,\"name\":\"전동멀티마스타(다용도톱)\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B500021\",\"buyCode\":null,\"engName\":\"ElectricMultinasster\",\"spec\":\"250Q\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":10,\"tags\":\"\"},{\"id\":1436,\"toolDto\":{\"id\":142,\"name\":\"토크렌치발력패드\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400068\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"ST8\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":3,\"tags\":\"\"},{\"id\":1437,\"toolDto\":{\"id\":141,\"name\":\"토크렌치발력패드\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400067\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"ST-4\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":3,\"tags\":\"\"},{\"id\":1438,\"toolDto\":{\"id\":161,\"name\":\"마그네트자동절단기레일\",\"subGroupDto\":{\"id\":104,\"name\":\"기타\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B600003\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"고무-YK-72D/1M\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":2,\"tags\":\"\"},{\"id\":1439,\"toolDto\":{\"id\":157,\"name\":\"DC인버터\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B500052\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"48V/3000W\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":5,\"tags\":\"\"},{\"id\":1440,\"toolDto\":{\"id\":153,\"name\":\"충전해머드릴\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B500045\",\"buyCode\":null,\"engName\":\"CordlessHammerDrill\",\"spec\":\"18V보쉬\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":9,\"tags\":\"\"},{\"id\":1441,\"toolDto\":{\"id\":154,\"name\":\"전동코너드릴\",\"subGroupDto\":{\"id\":73,\"name\":\"전동공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B500046\",\"buyCode\":null,\"engName\":\"ElectricCornerDrill\",\"spec\":\"DA-3010F(마끼다)\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":6,\"tags\":\"\"}]},{\"id\":6501,\"workerDto\":{\"id\":6,\"name\":\"김상빈\",\"code\":\"100348\",\"password\":\"123\",\"partDto\":{\"id\":3,\"name\":\"-\",\"subPartDto\":{\"id\":2,\"name\":\"정비안전지원그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"leaderDto\":{\"id\":45,\"name\":\"전진우\",\"code\":\"100640\",\"password\":\"123\",\"partDto\":{\"id\":7,\"name\":\"정비1파트\",\"subPartDto\":{\"id\":5,\"name\":\"정비1그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"toolboxDto\":{\"id\":5222,\"name\":\"선강정비1실\",\"managerDto\":{\"id\":113,\"name\":\"강성곤\",\"code\":\"100001\",\"password\":\"123\",\"partDto\":{\"id\":13,\"name\":\"정비2파트\",\"subPartDto\":{\"id\":10,\"name\":\"정비2그룹\",\"latitude\":\"35.2319\",\"longitude\":\"128.86709\",\"mapScale\":\"1\",\"mainPartDto\":{\"id\":1,\"name\":\"선강정비1실\",\"latitude\":null,\"longitude\":null,\"mapScale\":null}}},\"role\":\"USER\",\"employmentStatus\":\"EMPLOYMENT\"},\"systemOperability\":false},\"status\":\"REQUEST\",\"eventTimestamp\":\"2023-11-20T17:44:40.371734\",\"toolList\":[{\"id\":6502,\"toolDto\":{\"id\":122,\"name\":\"유압토크렌치소켓\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400023\",\"buyCode\":null,\"engName\":\"HydraulicTorqueWrenchSocket\",\"spec\":\"\\\"2\\\"\\\"115mm\\\"\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":6503,\"toolDto\":{\"id\":124,\"name\":\"유압파이프벤더\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400026\",\"buyCode\":null,\"engName\":\"HydraulicPipeBender\",\"spec\":\"1/2'~2'\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":6504,\"toolDto\":{\"id\":131,\"name\":\"유압알미늄잭램\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400045\",\"buyCode\":null,\"engName\":\"HydraulicAluminumJackRam\",\"spec\":\"TAR-10050/100Ton/193mm\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":6505,\"toolDto\":{\"id\":141,\"name\":\"토크렌치발력패드\",\"subGroupDto\":{\"id\":113,\"name\":\"유압공구\",\"mainGroupDto\":{\"id\":70,\"name\":\"내구성공기구\"}},\"code\":\"B400067\",\"buyCode\":null,\"engName\":\"\",\"spec\":\"ST-4\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"},{\"id\":6506,\"toolDto\":{\"id\":1350,\"name\":\"파이프컷터(방폭)\",\"subGroupDto\":{\"id\":1287,\"name\":\"방폭공구\",\"mainGroupDto\":{\"id\":1252,\"name\":\"자산성공기구\"}},\"code\":\"A310024\",\"buyCode\":null,\"engName\":\"PipeCutter\",\"spec\":\"\\\"8'~12\\\"\\\"\\\"\",\"unit\":\"EA\",\"price\":0,\"replacementCycle\":0},\"count\":1,\"tags\":\"\"}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":1,\"totalElements\":3,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":3,\"empty\":false}"
        val pagedata: Page = gson.fromJson(rentalrequestSheetListPageString, Page::class.java)
        val listRentalRequestSheetDto = object : TypeToken<List<RentalRequestSheetDto>>(){}.type
        val rentalRequestSheetDtoList: List<RentalRequestSheetDto> = gson.fromJson(gson.toJson(pagedata.content), listRentalRequestSheetDto)
        Log.d("TSTST", rentalRequestSheetDtoList.toString())
        return rentalRequestSheetDtoList
    }
}