import com.mongodb.client.*
import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import kotlinx.serialization.*
import org.bson.conversions.Bson
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import javax.management.QueryExp

@Serializable
data class Station(var _id: Id<String> = newId(), val address:String)
@Serializable
data class Allergens(val Birch:String?, val Alder:String?, val Grasses:String?, val OliveTree:String?)
@Serializable
data class Pollutants(val pm10: String?, val pm2_5:String?, val no2:String?)
@Serializable
data class Data(val allergens: Allergens, val pollutants: Pollutants, val aqi:String, val fromStation:Station? = null)

class DbConnection(url: String?, dbName: String) {
    private val client: MongoClient
    private val database: MongoDatabase


    init {
        client = KMongo.createClient(url!!)
        database = client.getDatabase(dbName)
    }

    fun insertData(data: List<Data>): InsertManyResult = database.getCollection<Data>("datas").insertMany(data)
    fun getStations(): MongoCollection<Station> = database.getCollection<Station>("stations")
    fun getData() : MongoCollection<Data> = database.getCollection<Data>("datas")

}
