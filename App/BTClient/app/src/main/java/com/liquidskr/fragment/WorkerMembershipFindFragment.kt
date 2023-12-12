package com.liquidskr.fragment

import SharedViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liquidskr.btclient.DatabaseHelper
import com.liquidskr.btclient.MembershipAdapter
import com.liquidskr.btclient.R
import com.mrsmart.standard.membership.MembershipSQLite


class WorkerMembershipFindFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var MainPartSpinner: Spinner
    private lateinit var SubPartSpinner: Spinner
    private lateinit var PartSpinner: Spinner

    val mainPartData = arrayOf("선강정비1실", "선강정비2실", "선강정비3실", "선강정비4실", "선강정비5실")

    val sungang1 = arrayOf("정비1그룹", "정비2그룹", "정비3그룹", "정비안전지원그룹")
    val sungang2 = arrayOf("정비1그룹", "정비2그룹", "정비안전지원그룹")
    val sungang3 = arrayOf("정비1그룹", "정비2그룹", "정비3그룹", "정비안전지원그룹")
    val sungang4 = arrayOf("정비1그룹", "정비2그룹", "정비안전지원섹션", "정비안전지원그룹")
    val sungang5 = arrayOf("정비1그룹", "정비2그룹", "정비안전지원섹션")

    var subPartData = sungang1

    val group1_1 = arrayOf("-", "정비1파트", "정비2파트", "정비3파트")
    val group1_2 = arrayOf("-", "정비1파트", "정비2파트")
    val group1_3 = arrayOf("-", "정비1파트", "정비2파트")
    val group1_g = arrayOf("-", "정비안전지원섹션")

    val group2_1 = arrayOf("-", "정비1파트", "정비2파트", "정비3파트")
    val group2_2 = arrayOf("-", "정비1파트", "정비2파트")
    val group2_g = arrayOf("-", "정비안전지원섹션")

    val group3_1 = arrayOf("-", "정비1파트")
    val group3_2 = arrayOf("-", "정비1파트", "정비2파트")
    val group3_3 = arrayOf("-", "정비1파트", "정비2파트")
    val group3_g = arrayOf("-", "정비안전지원섹션")

    val group4_1 = arrayOf("-", "정비1파트", "정비2파트")
    val group4_2 = arrayOf("-", "정비1파트", "정비2파트")
    val group4_s = arrayOf("-")
    val group4_g = arrayOf("-")

    val group5_1 = arrayOf("-", "정비1파트", "정비2파트")
    val group5_2 = arrayOf("-", "정비1파트", "정비2파트")
    val group5_s = arrayOf("-")

    var partData = group1_1

    private val sharedViewModel: SharedViewModel by lazy { // Access to SharedViewModel
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_membership_list, container, false)
        MainPartSpinner = view.findViewById(R.id.MainPartSpinner)
        SubPartSpinner = view.findViewById(R.id.SubPartSpinner)
        PartSpinner = view.findViewById(R.id.PartSpinner)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mainPartData)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        MainPartSpinner.adapter = adapter1

        MainPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, mainPosition: Int, id: Long) {
                // 선택된 MainPartSpinner 항목에 따라 subPartData 업데이트
                when (mainPosition) {
                    0 -> {
                        subPartData = sungang1
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPartData)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SubPartSpinner.adapter = adapter2
                        // MainPartSpinner가 0일때
                        SubPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, subPosition: Int, id: Long) {
                                // 선택된 SubPartSpinner 항목에 따라 partData 업데이트
                                when (subPosition) {
                                    0 -> partData = group1_1
                                    1 -> partData = group1_2
                                    2 -> partData = group1_3
                                    3 -> partData = group1_g
                                }

                                // partData로 PartSpinner 업데이트
                                val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partData)
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                PartSpinner.adapter = adapter3
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // 아무것도 선택되지 않았을 때의 동작
                            }
                        }
                    }
                    1 -> {
                        subPartData = sungang2
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPartData)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SubPartSpinner.adapter = adapter2
                        // MainPartSpinner가 1일때
                        SubPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, subPosition: Int, id: Long) {
                                // 선택된 SubPartSpinner 항목에 따라 partData 업데이트
                                when (subPosition) {
                                    0 -> partData = group2_1
                                    1 -> partData = group2_2
                                    2 -> partData = group2_g
                                }

                                // partData로 PartSpinner 업데이트
                                val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partData)
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                PartSpinner.adapter = adapter3
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // 아무것도 선택되지 않았을 때의 동작
                            }
                        }
                    }
                    2 -> {
                        subPartData = sungang3
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPartData)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SubPartSpinner.adapter = adapter2
                        // MainPartSpinner가 1일때
                        SubPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, subPosition: Int, id: Long) {
                                // 선택된 SubPartSpinner 항목에 따라 partData 업데이트
                                when (subPosition) {
                                    0 -> partData = group3_1
                                    1 -> partData = group3_2
                                    2 -> partData = group3_3
                                    3 -> partData = group3_g
                                }

                                // partData로 PartSpinner 업데이트
                                val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partData)
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                PartSpinner.adapter = adapter3
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // 아무것도 선택되지 않았을 때의 동작
                            }
                        }
                    }
                    3 -> {
                        subPartData = sungang4
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPartData)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SubPartSpinner.adapter = adapter2
                        // MainPartSpinner가 1일때
                        SubPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, subPosition: Int, id: Long) {
                                // 선택된 SubPartSpinner 항목에 따라 partData 업데이트
                                when (subPosition) {
                                    0 -> partData = group4_1
                                    1 -> partData = group4_2
                                    2 -> partData = group4_s
                                    3 -> partData = group4_g
                                }

                                // partData로 PartSpinner 업데이트
                                val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partData)
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                PartSpinner.adapter = adapter3
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // 아무것도 선택되지 않았을 때의 동작
                            }
                        }
                    }
                    4 -> {
                        subPartData = sungang5
                        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subPartData)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SubPartSpinner.adapter = adapter2
                        // MainPartSpinner가 1일때
                        SubPartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, subPosition: Int, id: Long) {
                                // 선택된 SubPartSpinner 항목에 따라 partData 업데이트
                                when (subPosition) {
                                    0 -> partData = group5_1
                                    1 -> partData = group5_2
                                    2 -> partData = group5_s
                                }

                                // partData로 PartSpinner 업데이트
                                val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, partData)
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                PartSpinner.adapter = adapter3
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // 아무것도 선택되지 않았을 때의 동작
                            }
                        }
                    }
                }

                // Update SubPartSpinner based on the selected main position
                // (You might need to set default values or handle other cases)
                // ...
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 동작
            }
        }

        // DatabaseHelper 인스턴스 생성
        val databaseHelper = DatabaseHelper(requireContext())
        val memberships: List<MembershipSQLite> = databaseHelper.getAllMemberships()

        val adapter = MembershipAdapter(memberships) { membership ->
            val type = getType()

            if (type == 1) {
                sharedViewModel.worker = membership
                Log.d("test", membership.toString() + "///" + sharedViewModel.worker.toString())

            } else if (type == 2) {
                sharedViewModel.leader = membership
                Log.d("test", membership.toString() + "///" + sharedViewModel.leader.toString())
            }

            // 새로운 Fragment 생성 (Fragment 백스택)
            val fragment = WorkerRentalFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }

        recyclerView.adapter = adapter

        return view
    }
    companion object {
        private var type: Int = 0

        fun getType(): Int {
            return type
        }
        fun newInstance(type: Int): WorkerMembershipFindFragment {
            val fragment = WorkerMembershipFindFragment()
            this.type = type
            return fragment
        }
    }
}