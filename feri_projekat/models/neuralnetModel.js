var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var neuralnetSchema = new Schema({
	'mode': String,
	'polluted': Boolean,
	'lat': Number,
	'long': Number
});

module.exports = mongoose.model('neuralnet', neuralnetSchema);
