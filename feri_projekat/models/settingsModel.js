var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var settingsSchema = new Schema({
	'verbose' : Boolean,
	'stations' : String,
	'interval' : Number,
	'unit' : String,
	'token' : String
});

module.exports = mongoose.model('settings', settingsSchema);
