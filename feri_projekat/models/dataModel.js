var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'date' : Date,
	'pollutants' : [{
		'AQI' : String,
		'PM2_5' : String,
		'PM10' : String,
		'NO2' : String
	}],
	'allergens' : [{
		'Birch' : String,
		'Alder' : String,
		'Grasses' : String,
		'Ragweed' : String,
		'Mugwort' : String,
		'OliveTree' : String
	}],
	'fromStation' : {
		type: Schema.Types.ObjectId,
		ref: 'station'}
});

module.exports = mongoose.model('data', dataSchema);
