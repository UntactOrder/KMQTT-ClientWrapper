package io.github.untactorder.test.toasterAtSnackbar

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import io.github.untactorder.test.common.AndroidApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            AndroidApp()
        }
    }
}

/*
@Preview
@Composable
fun Greeting2(name: String = "World") {
    Text(text = "Hello $name!")
}

@Preview
@Composable
fun ShowAge(value: MutableState<Int> = rememberSaveable { mutableStateOf(0) }) {
    Text(text = value.value.toString(), style = TextStyle(
        color = Color.Red, fontSize = 35.sp,
        fontWeight = FontWeight.ExtraBold),
        modifier = Modifier.clickable {
            value.value++
            println("ShowAge : " + value.value.toString())
        }
    )
}

@Preview
@Composable
fun CreateCircle(value: MutableState<Int> = rememberSaveable { mutableStateOf(0) }) {
    Card(modifier = Modifier
        .padding(3.dp)
        .size(50.dp),
        shape = CircleShape
    ) {
        Text(text = value.value.toString(), style = TextStyle(
            color = Color.Red, fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.clickable {
                value.value++
                println("CreateCircle : " + value.value.toString())
            }
        )
    }
}

@Preview
@Composable
fun ShowCard() {
    Card(
        shape = RoundedCornerShape(3.dp),
        border = BorderStroke(2.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color.Gray),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .weight(1f, fill = false)
        ) {
            Text(
                text = "Jetpack",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier
                .height(100.dp)
                .aspectRatio(0.2f)
                .background(Color.Blue))
            Text(
                text = "Compose"
            )
        }
    }
}

@Preview(name = "Greeting", showBackground = true)
@Composable
fun DefaultPreview() {
    // A surface container using the 'background' color from the theme
    val conStat: MutableState<Int> = rememberSaveable { mutableStateOf(0) }
    UntactOrderApplicationTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 20.dp),
            color = MaterialTheme.colorScheme.getSignature().background
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Greeting2("Android" + Random.nextInt())
                ShowAge(conStat)
                ShowCard()
                CreateCircle(conStat)
            }
        }
    }
}
*/
