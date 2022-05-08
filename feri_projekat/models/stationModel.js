var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var stationSchema = new Schema({
	'address' : String,
	'geolocation' : String,
	'date' : { type: Date, default: Date.now}
});

module.exports = mongoose.model('station', stationSchema);
