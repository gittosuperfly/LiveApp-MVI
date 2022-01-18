package cn.cai.star.liveapp.feature.live.list

import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class LiveListViewModel @ViewModelInject constructor(
    private val repo: LiveListRepository
) : ViewModel() {
    fun getAllLive() = repo.getAllLive()
}