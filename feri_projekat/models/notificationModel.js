var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var NotificationSchema = new Schema({
	'content' : String,
	'topic' : String,
	'date' : { type: Date, default: Date.now},
	'latitude' : String,
	'longitude' : String
});

module.exports = mongoose.model('Notification', NotificationSchema);
