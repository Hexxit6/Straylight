var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

/*var allergensSchema = new Schema({
	'Birch' : String,
	'Alder' : String,
	'Grasses' : String,
	'Ragweed' : String,
	'Mugwort' : String,
	'OliveTree' : String
	});
var pollutantsSchema = new Schema({
	'AQI' : String,
	'PM2_5' : String,
	'PM10' : String,
	'NO2' : String
});
var dataSchema = new Schema({
	'allergens': allergensSchema,
	'pollutants': pollutantsSchema,
	'fromStation' : {
		type: Schema.Types.ObjectId,
		ref: 'station'}
});

 */
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
	'fromStation' : {
		type: Schema.Types.ObjectId,
		ref: 'station'}
});


module.exports = mongoose.model('data', dataSchema);
