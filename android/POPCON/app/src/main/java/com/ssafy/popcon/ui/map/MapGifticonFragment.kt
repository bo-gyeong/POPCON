package com.ssafy.popcon.ui.map
/*
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentMapGiftconBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.Utils
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
    private var gifticonList = mutableListOf<Gifticon>()


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
        binding = DataBindingUtil.inflate(inflater, R.layout.item_map_giftcon, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBrandMap.text = param1

        // TODO 현 위치 기반 기프티콘 전체 받아오기
        gifticonList.add(
            Gifticon(
                "123123123","https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004429]_20221121150147483.jpg",
                Brand(
                    "스타벅스",
                    "https://user-images.githubusercontent.com/33195517/211953130-74830fe3-a9e1-4faa-a4fd-5c4dac0fcb63.png"
                ),
                "2023-01-22 00:00:00",
                0,10000, "유라",
                "https://image.istarbucks.co.kr/upload/store/skuimg/2022/11/[9300000004429]_20221121150147483.jpg",
                "마스카포네 치즈 케이크",
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
            )
        )

        binding.badge = Utils.calDday(gifticonList[0])

        */
/*giftconList.add(
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
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
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
                "https://user-images.githubusercontent.com/33195517/214460267-7db6d578-3779-4f12-91b4-6deaf2ff82d2.png",
                makeDateTimeException("2023-01-24 00:00:00"),
                Badge("D-${findRemainingDay("2023-01-24 00:00:00")}", color = "#333")
            )
        )*//*

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
*/
