var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'pollutants' : {
		'AQI' : String,
		'PM2_5' : String,
		'PM10' : String,
		'NO2' : String
	},
	'allergens' : {
		'Birch' : String,
		'Alder' : String,
		'Grasses' : String,
		'Ragweed' : String,
		'Mugwort' : String,
		'OliveTree' : String
	},
	'date' : Date,
	'fromStation' : {
		type: Schema.Types.String,
		ref: 'station'}
});


module.exports = mongoose.model('data', dataSchema);
