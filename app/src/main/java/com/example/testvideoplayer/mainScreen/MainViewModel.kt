package com.example.testvideoplayer.mainScreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testvideoplayer.api.model.ListData
import com.example.testvideoplayer.api.model.NewData
import com.example.testvideoplayer.db.Repository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: Repository
) : ViewModel() {

    var listBanners: List<NewData> by mutableStateOf(listOf())

    val player = ExoPlayer.Builder(context).build()

    private var _visible: MutableLiveData<Boolean> = MutableLiveData(false)
    val visible : LiveData<Boolean>
        get() = _visible

    init {
        getListBanner()
    }

    fun changeVisible(){
        when (visible.value) {
            true -> _visible.value = false
            else -> _visible.value = true
        }
    }

    fun prepareMedia(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.play()
    }

    private fun getListBanner() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getList(object : Callback<ListData> {
                override fun onResponse(
                    call: Call<ListData>,
                    response: Response<ListData>
                ) {
                    response.body()?.let { responseBody ->
                        val list = mutableListOf<NewData>()
                        responseBody.forEach {
                            list.add(NewData(it.file_url, it.small_thumbnail_url))
                        }
                        listBanners = list
                        prepareMedia(list[0].file_url)
                    }
                }

                override fun onFailure(call: Call<ListData>, t: Throwable) {
                }
            })
        }
    }
}