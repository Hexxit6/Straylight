var SettingsModel = require('../models/settingsModel.js');

/**
 * settingsController.js
 *
 * @description :: Server-side logic for managing settings.
 */
module.exports = {

    start: function (req, res) {
        SettingsModel.find(function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings.',
                    error: err
                });
            }

            return res.json(settings);
        });
    },

    stop: function (req, res) {
        SettingsModel.find(function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings.',
                    error: err
                });
            }

            return res.json(settings);
        });
    },

    /**
     * settingsController.list()
     */
    list: function (req, res) {
        SettingsModel.find(function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings.',
                    error: err
                });
            }

            return res.json(settings);
        });
    },

    /**
     * settingsController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        SettingsModel.findOne({_id: id}, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting settings.',
                    error: err
                });
            }

            if (!settings) {
                return res.status(404).json({
                    message: 'No such settings'
                });
            }

            return res.json(settings);
        });
    },

    /**
     * settingsController.create()
     */
    create: function (req, res) {
        var settings = new SettingsModel({
			verbose : req.body.verbose,
			stations : req.body.stations,
			interval : req.body.interval,
			unit : req.body.unit,
			token : req.body.token
        });

        settings.save(function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating settings',
                    error: err
                });
            }

            return res.status(201).json(settings);
        });
    },

    /**
     * settingsController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        SettingsModel.findOne({_id: id}, function (err, settings) {
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

    /**
     * settingsController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        SettingsModel.findByIdAndRemove(id, function (err, settings) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the settings.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
