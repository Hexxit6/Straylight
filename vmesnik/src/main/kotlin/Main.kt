// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.result.InsertManyResult
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.litote.kmongo.*
import java.io.File
import java.io.InputStream


//allergens
var newBirch1 = ""
var newBirch2 = ""
var newAlder1 = ""
var newAlder2 = ""
var newGrasses1 = ""
var newGrasses2 = ""
var newOliveTree1 = ""
var newOliveTree2 = ""
var newAqi1 = ""
var newAqi2 = ""
//pollutants
var newPM10_1 = ""
var newPM10_2 = ""
var newPM2_5_1 = ""
var newPM2_5_2 = ""
var newNO2_1 = ""
var newNO2_2 = ""
var newInsertField = ""
var birch = 0
var alder = 0
var grasses = 0
var oliveTRee = 0
var aqi = 0
var pm10 = 0
var pm2_5 = 0
var no2 = 0
var data = mutableListOf<Data>()
var newData: InsertManyResult? = null


fun randomGenerator(x: Int, y: Int): Int {
    return (x until y).random()

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun app(
    db: DbConnection,
    stations: MongoCollection<Station>,
    datas: MongoCollection<Data>
) {
    //allergens
    var birchField1 by remember { mutableStateOf("0") }
    var birchField2 by remember { mutableStateOf("0") }
    var alderField1 by remember { mutableStateOf("0") }
    var alderField2 by remember { mutableStateOf("0") }
    var grassesField1 by remember { mutableStateOf("0") }
    var grassesField2 by remember { mutableStateOf("0") }
    var oliveTreeField1 by remember { mutableStateOf("0") }
    var oliveTreeField2 by remember { mutableStateOf("0") }
    var aqiField1 by remember { mutableStateOf("0") }
    var aqiField2 by remember { mutableStateOf("0") }
    //pollutants
    var pm10Field1 by remember { mutableStateOf("0") }
    var pm10Field2 by remember { mutableStateOf("0") }
    var pm2_5Field1 by remember { mutableStateOf("0") }
    var pm2_5Field2 by remember { mutableStateOf("0") }
    var no2Field1 by remember { mutableStateOf("0") }
    var no2Field2 by remember { mutableStateOf("0") }
    var insertField by remember { mutableStateOf("0") }

    val showDialog = remember { mutableStateOf(false) }

    var stationList = mutableStateOf(stations.find())
    val stationData = (stationList.value).mapIndexed { index, st ->
        index to st
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Interface",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 10.dp
            )
        },
        bottomBar = {
            BottomAppBar(backgroundColor = MaterialTheme.colors.primary)
            {
                Text(
                    text = "Straylight",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    ) {

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //titles
            Row(
                modifier = Modifier.width(500.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Allergens: ", fontSize = 30.sp)
                Text("Pollutants: ", fontSize = 30.sp)
            }
            //fields for inserting data
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text("Birch: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = birchField1,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        birchField1 = it
                    }
                )
                Text(" to: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = birchField2,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        birchField2 = it
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(400.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AQI:     ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = aqiField1,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            aqiField1 = it
                        }
                    )
                    Text(" to: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = aqiField2,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            aqiField2 = it
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text("Alder: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = alderField1,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        alderField1 = it
                    }
                )
                Text(" to: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = alderField2,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        alderField2 = it
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(400.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text("PM10: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = pm10Field1,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            pm10Field1 = it
                        }
                    )
                    Text(" to: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = pm10Field2,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            pm10Field2 = it
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text("Grasses: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = grassesField1,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        grassesField1 = it
                    }
                )
                Text(" to: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = grassesField2,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        grassesField2 = it
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(400.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text("PM2.5: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = pm2_5Field1,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            pm2_5Field1 = it
                        }
                    )
                    Text(" to: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = pm2_5Field2,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            pm2_5Field2 = it
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text("Olive Tree: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = oliveTreeField1,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        oliveTreeField1 = it
                    }
                )
                Text(" to: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = oliveTreeField2,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        oliveTreeField2 = it
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .width(400.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text("NO2: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = no2Field1,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            no2Field1 = it
                        }
                    )
                    Text(" to: ")
                    OutlinedTextField(
                        modifier = Modifier.width(80.dp),
                        value = no2Field2,
                        label = { Text(text = "Value") },
                        onValueChange = {
                            no2Field2 = it
                        }
                    )
                }
            }


                Text("Number of inserts: ")
                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = insertField,
                    label = { Text(text = "Value") },
                    onValueChange = {
                        insertField = it
                    }
                )

                Row(
                    modifier = Modifier
                        .padding(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stationData.forEach {
                        Button(onClick = {
                            //allergens
                            newBirch1 = birchField1
                            newBirch2 = birchField2
                            newAlder1 = alderField1
                            newAlder2 = alderField2
                            newGrasses1 = grassesField1
                            newGrasses2 = grassesField2
                            newOliveTree1 = oliveTreeField1
                            newOliveTree2 = oliveTreeField2
                            //pollutants
                            newAqi1 = aqiField1
                            newAqi2 = aqiField2
                            newPM10_1 = pm10Field1
                            newPM10_2 = pm10Field2
                            newPM2_5_1 = pm2_5Field1
                            newPM2_5_2 = pm2_5Field2
                            newNO2_1 = no2Field1
                            newNO2_2 = no2Field2
                            newInsertField = insertField

                            data = mutableListOf()
                            for (i in 1..newInsertField.toInt()) {
                                //allergens
                                birch = randomGenerator(newBirch1.toInt(), newBirch2.toInt())
                                alder = randomGenerator(newAlder1.toInt(), newAlder2.toInt())
                                grasses = randomGenerator(newGrasses1.toInt(), newGrasses2.toInt())
                                oliveTRee = randomGenerator(newOliveTree1.toInt(), newOliveTree2.toInt())
                                //pollutants
                                aqi = randomGenerator(newAqi1.toInt(), newAqi2.toInt())
                                pm10 = randomGenerator(newPM10_1.toInt(), newPM10_2.toInt())
                                pm2_5 = randomGenerator(newPM2_5_1.toInt(), newPM2_5_2.toInt())
                                no2 = randomGenerator(newNO2_1.toInt(), newNO2_2.toInt())
                                var allergens = Allergens(
                                    birch.toString(),
                                    alder.toString(),
                                    grasses.toString(),
                                    oliveTRee.toString()
                                )
                                var pollutants = Pollutants(pm10.toString(), pm2_5.toString(), no2.toString())
                                data.add(Data(allergens, pollutants, aqi.toString()))
                            }
                            newData = db.insertData(data)
                            datas.updateMany(
                                (Filters.eq("fromStation", null)), //"${it.second.address}")),
                                setValue<Station>(Data::fromStation, it.second)
                            )
                            showDialog.value = true

                        }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "content description", Modifier.padding(0.dp))
                            Text("${it.second.address}")
                        }
                    }
                    if(showDialog.value) {
                        if(newData?.wasAcknowledged() == true) {
                            AlertDialog(
                                onDismissRequest =  { showDialog.value = false},
                                confirmButton = {
                                    TextButton(onClick = {showDialog.value = false})
                                    { Text(text = "OK") }
                                },
                                text = { Text(text = "Data is successfully inserted.", fontWeight = FontWeight.Bold) },
                                backgroundColor = Color.Gray,
                                modifier = Modifier.width(400.dp)
                            )
                        }
                        else {
                            AlertDialog(
                                onDismissRequest =  { showDialog.value = false},
                                confirmButton = {
                                    TextButton(onClick = {showDialog.value = false})
                                    { Text(text = "OK") }
                                },
                                text = { Text(text = "Inserted failed.") }
                            )
                        }
                    }
                }
            }



            /* Button(onClick = { db.insertData(data)
                     //datas.updateOne((Filters.eq("${Data::fromStation}","${stationData.second.address}")), setValue(Data::fromStation, stationData.second._id))
                     //kako koristiti opciju updateOne?
                     //postoji li drugi nacin za tuji kljuc

         }
         ) {
             Icon(Icons.Default.Add, contentDescription = "content description", Modifier.padding(0.dp))
             Text("Insert data")
         }
         */
            /* for(x in station) {
             stationData.forEach {
                 if (it.second.address != x.address) {
                     Button(onClick = { db.insertStation(station) }
                     ) {
                         Icon(Icons.Default.Add, contentDescription = "content description", Modifier.padding(0.dp))
                         Text("Insert stations")
                     }
                 }
             }
         }
*/


            /*   Button(onClick = {
                       stationData.forEach {
                           for (x in station) {
                           if (!it.second.address.equals(x.address)) {
                               println("true: ${it.second.address} || ${x.address}")
                               db.insertStation(station)
                           } else {
                               println("false: ${it.second.address} || ${x.address}")
                           }
                       }

                   }
               }
               ) {
                   Icon(Icons.Default.Add, contentDescription = "content description", Modifier.padding(0.dp))
                   Text("Insert stations")
               }

             */


        }
    }

//}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Interface") {
        val inputStream: InputStream =
            File("C:\\Users\\Andjela\\Desktop\\vmesnik\\src\\main\\kotlin\\config.json").inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        var jsonObject = Json.decodeFromString<HashMap<String, String>>(inputString)
        var url = jsonObject["url"]
        val db = DbConnection(
            url,
            "projekat"
        )

        app(db, db.getStations(), db.getData())

    }
}
