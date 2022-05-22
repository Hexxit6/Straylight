var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var commentSchema = new Schema({
	'content' : String,
	'datetime' : Date,
	'name' : String,
	'email' : String,
	'id_user' : {
		type: Schema.Types.ObjectId,
		ref: 'user'},
	'id_station' : {
		type: Schema.Types.ObjectId,
		ref: 'station'}
});

module.exports = mongoose.model('comment', commentSchema);
