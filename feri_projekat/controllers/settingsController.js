var SettingsModel = require('../models/settingsModel.js');
const { spawn } = require('node:child_process');


/**
 * settingsController.js
 *
 * @description :: Server-side logic for managing settings.
 */
module.exports = {

    start: function (req, res) {
        SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings.',
                    error: err
                });
            }

			const ps = spawn('python3', [`${__dirname}/../principi/scrapper.py`, '-s', `${__dirname}/../principi/stations_active.txt`, '-i', settings.interval, '-u', settings.unit, '-t', settings.token])

            return res.json(settings);
        });
    },

    stop: function (req, res) {
		const ps = spawn('killall', ['python3'])
		return res.status(200);
    },

	upload: function (req, res) {
		return res.status(200).json({
			message: "File uploaded successfully!"
		});
	},

	interval: function (req, res) {
        SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings',
                    error: err
                });
            }
            if (!settings) {
                return res.status(404).json({
                    message: 'No such settings'
                });
            }

			settings.interval = req.body.interval ? req.body.interval : settings.interval;
			
            settings.save(function (err, settings) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating settings.',
                        error: err
                    });
                }
                return res.json(settings);
            });
        });
	},

	unit: function (req, res) {
        SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings',
                    error: err
                });
            }
            if (!settings) {
                return res.status(404).json({
                    message: 'No such settings'
                });
            }

			settings.unit = req.body.unit ? req.body.unit : settings.unit;
			
            settings.save(function (err, settings) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating settings.',
                        error: err
                    });
                }
                return res.json(settings);
            });
        });
	},

	token: function (req, res) {
        SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings',
                    error: err
                });
            }
            if (!settings) {
                return res.status(404).json({
                    message: 'No such settings'
                });
            }

			settings.token = req.body.token ? req.body.token : settings.token;
			
            settings.save(function (err, settings) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating settings.',
                        error: err
                    });
                }
                return res.json(settings);
            });
        });
	},
	
    /**
     * settingsController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        SettingsModel.findOne({}, {}, {sort: {_id: -1}}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings',
                    error: err
                });
            }

            if (!settings) {
                return res.status(404).json({
                    message: 'No such settings'
                });
            }

            settings.verbose = req.body.verbose ? req.body.verbose : settings.verbose;
			settings.stations = req.body.stations ? req.body.stations : settings.stations;
			settings.interval = req.body.interval ? req.body.interval : settings.interval;
			settings.unit = req.body.unit ? req.body.unit : settings.unit;
			settings.token = req.body.token ? req.body.token : settings.token;
			
            settings.save(function (err, settings) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating settings.',
                        error: err
                    });
                }

                return res.json(settings);
            });
        });
    },

};
