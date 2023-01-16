package com.ssafy.popcon.ui.add

// 리사이클러뷰의 item클릭시 AddFragment의 메소드에 접근하기위한 interface
interface onItemClick {
    fun onClick(idx: Int)
}