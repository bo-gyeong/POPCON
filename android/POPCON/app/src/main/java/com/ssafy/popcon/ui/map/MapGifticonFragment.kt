package com.ssafy.popcon.ui.map

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentMapGiftconBinding
import com.ssafy.popcon.dto.Badge
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.MainActivity
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MapGiftconFragment 지원"

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MapGifticonFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMapGiftconBinding
    private var giftconList = mutableListOf<Gifticon>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_giftcon, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBrandMap.text = param1

        giftconList.add(
            Gifticon(
                "123123123",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png"
                ),
                "마스카포네 치즈 타르트",
                0,
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004429]_20221121150147483.jpg",
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004429]_20221121150147483.jpg",
                makeDateTimeException("2023-01-22 00:00:00"),
                Badge("D-${findRemainingDay("2023-01-22 00:00:00")}", color = "#333")
            )
        )

        giftconList.add(
            Gifticon(
                "123123123",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png"
                ),
                "나이트로 콜드 브루",
                0,
                "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[9200000000479]_20210426091844065.jpg",
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004429]_20221121150147483.jpg",
                makeDateTimeException("2023-01-23 00:00:00"),
                Badge("D-${findRemainingDay("2023-01-23 00:00:00")}", color = "#333")
            )
        )

        giftconList.add(
            Gifticon(
                "123123123",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png"
                ),
                "당근 피칸 케이크",
                0,
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004430]_20221121150217180.jpg",
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004430]_20221121150217180.jpg",
                makeDateTimeException("2023-01-24 00:00:00"),
                Badge("D-${findRemainingDay("2023-01-24 00:00:00")}", color = "#333")
            )
        )
        binding.gifitcon = giftconList.get(param1!!.toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapGifticonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

fun makeDateTimeException(eventDate: String): String {
//    eventDate : 2023-07-14 10:12:14
    val cal = Calendar.getInstance()
    var t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
    val date: Date = t_dateFormat.parse(eventDate)
    cal.time = date
    return "${t_dateFormat.format(cal.time)}"
}


fun findRemainingDay(eventDate: String): Int {
//    eventDate : 2023-07-14 10:12:14
    var today = Calendar.getInstance()
    var sf = SimpleDateFormat("yyyy-MM-dd 00:00:00")
    var eventDate = sf.parse(eventDate)
    val remainingDay = (eventDate.time - today.time.time) / (60 * 60 * 24 * 1000)
    return remainingDay.toInt()
}