package com.example.testvideoplayer.mainScreen

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testvideoplayer.api.model.NewData
import kotlin.math.roundToInt

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {

    val list = viewModel.listBanners
    val player = viewModel.player
    val visible by viewModel.visible.observeAsState()

    Column(
        modifier = Modifier.background(Color.White)
    ) {
        VideoPlayer(player, visible!!)
        TextButton(viewModel)
        VideosRow(list, viewModel)
    }
}

@Composable
fun VideoPlayer(player: ExoPlayer, visible: Boolean) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(25.dp)
            .clipToBounds()
    ) {
        if (visible) {
            Box(
                modifier = Modifier
                    .defaultMinSize()
                    .zIndex(2f)
                    .align(alignment = Alignment.Center)
                    .offset {
                        IntOffset(
                            offsetX.roundToInt(),
                            offsetY.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                ChangedTextField()
            }
        }
        AndroidView(
            modifier = Modifier
                .zIndex(1f)
                .wrapContentSize()
                .padding(30.dp),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                }
            }
        )
    }
}

@Composable
fun ChangedTextField() {
    val text = remember { mutableStateOf("test") }
    TextField(
        modifier = Modifier
            .wrapContentSize(),
        value = text.value,
        onValueChange = { text.value = it },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.Green,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun TextButton(viewModel: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { viewModel.changeVisible() }) {
            Text(
                text = "Text",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideosRow(list: List<NewData>, viewModel: MainViewModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        items(list) { item ->
            Card(
                onClick = { viewModel.prepareMedia(item.file_url) },
                modifier = Modifier
                    .padding(15.dp)
                    .background(Color.White)
                    .width(50.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.small,
                elevation = 4.dp
            ) {
                GlideImage(
                    imageModel =
                    item.small_poster_url,
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    )
                )
            }
        }
    }
}