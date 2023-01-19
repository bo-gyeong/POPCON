package com.ssafy.popcon.ui.map

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.popup.GifticonPreviewFragment.Companion.newInstance
import com.ssafy.popcon.ui.popup.GifticonViewFragment.Companion.newInstance

private const val TAG = "NoticeAdapter_싸피"


class MapGifticonAdpater(fa : FragmentActivity) : FragmentStateAdapter(fa){
    private var numPages = 3

    override fun getItemCount(): Int = numPages

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {MapGifticonFragment.newInstance("0", "")}
            1 -> {MapGifticonFragment.newInstance("1", "")}
            else -> {MapGifticonFragment.newInstance("2", "")}
        }
    }

}


















//class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.NoticeHolder>() {
//
//    private var notifications = sharedPreferencesUtil.getNotification()
//
//    fun setList(){
//        notifications = sharedPreferencesUtil.getNotification()
//        notifyDataSetChanged()
//    }
//
//    inner class NoticeHolder(val binding: List) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bindInfo(item: Notification) {
//            binding.noti = item.msg
//
//            binding.delBtn.setOnClickListener {
//                sharedPreferencesUtil.deleteNotification(bindingAdapterPosition)
//                this@NoticeAdapter.notifyItemRemoved(bindingAdapterPosition)
//                setList()
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
//        val listItemNoticeBinding =
//            ListItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return NoticeHolder(listItemNoticeBinding)
//    }
//
//    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
//        holder.bindInfo(notifications[position])
//    }
//
//    override fun getItemCount(): Int {
//        return notifications.size
//    }
//}
//
