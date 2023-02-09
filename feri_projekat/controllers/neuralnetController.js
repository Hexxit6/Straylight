var NeuralnetModel = require('../models/neuralnetModel.js');
const { spawn } = require('node:child_process');
var fs = require('fs');

module.exports = {

	image: function (req, res) {
		var image = req.body.file;
		var buffer = new Buffer(image, 'base64');
		fs.writeFile('/tmp/image.png', buffer);

		var dataToSend;
		const python = spawn('python3', ['pollution_model.pth', '/tmp/image.png']);
		python.stdout.on('data', function (data) {
			dataToSend = data.toString();
		});
		
		python.on('close', (code) => {
			res.send(dataToSend)
		});
		
		var data = new NeuralnetModel({
			mode: "image",
			polluted: req.body.mode,
			lat: req.body.lat,
			long: req.body.lng
		});
		
		data.save(function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating data',
                    error: err
                });
            }
            return res.status(201).json(data);
        });
	},

	sound: function (req, res) {
		var sound = req.body.file;
		var buffer = new Buffer(image, 'base64');
		fs.writeFile('/tmp/sound.png', buffer);

		var dataToSend;
		const python = spawn('python3', ['sound_pollution/main.py']);
		python.stdout.on('data', function (data) {
			dataToSend = data.toString();
		});
		
		python.on('close', (code) => {
			res.send(dataToSend)
		});
		
		var data = new NeuralnetModel({
			mode: "sound",
			polluted: req.body.mode,
			lat: req.body.lat,
			long: req.body.lng
		});
		
		data.save(function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating data',
                    error: err
                });
            }
            return res.status(201).json(data);
        });
	},

	simulation: function (req, res) {
		return res.status(200).json({
			message: "TODO"
		});
	}

};
