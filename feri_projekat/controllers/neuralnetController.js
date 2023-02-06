var NeuralnetModel = require('../models/neuralnetModel.js');
const { spawn } = require('node:child_process');

module.exports = {

   //  start: function (req, res) {
   //      SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
   //          if (err) {
   //              return res.status(500).json({
   //                  message: 'Error when getting settings.',
   //                  error: err
   //              });
   //          }
   //
			// const ps = spawn('python3', [`${__dirname}/../principi/scrapper.py`, '-s', `${__dirname}/../principi/stations_active.txt`, '-i', settings.interval, '-u', settings.unit, '-t', settings.token])
   //
   //          return res.json(settings);
   //      });
   //  },

	image: function (req, res) {
		return res.status(200).json({
			message: "TODO"
		});
	},

	sound: function (req, res) {
		return res.status(200).json({
			message: "TODO"
		});
	},

	simulation: function (req, res) {
		return res.status(200).json({
			message: "TODO"
		});
	}

};
