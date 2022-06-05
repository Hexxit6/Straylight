var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var dataSchema = new Schema({
	'pollutants' : {
		'AQI' : Number,
		'PM2_5' : Number,
		'PM10' : Number,
		'NO2' : Number
	},
	'allergens' : {
		'Birch' : Number,
		'Alder' : Number,
		'Grasses' : Number,
		'Ragweed' : Number,
		'Mugwort' : Number,
		'OliveTree' : Number
	},
	'date' : Date,
	'fromStation' : {
		type: Schema.Types.String,
		ref: 'station'}
});


module.exports = mongoose.model('data', dataSchema);
