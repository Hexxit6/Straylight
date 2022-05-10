var StationModel = require('../models/stationModel.js');
var DataModel = require('../models/dataModel');

/**
 * stationController.js
 *
 * @description :: Server-side logic for managing stations.
 */
module.exports = {

    /**
     * stationController.list()
     */
    list: function (req, res) {
        StationModel.find(function (err, stations) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station.',
                    error: err
                });
            }
            var data = [];
            data.stations = stations;
            //return res.json(stations);
            return res.render('station/all_stations', data)
        });
    },

    /**
     * stationController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        //req.session.stationID = id;

        StationModel.findOne({_id: id}, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station.',
                    error: err
                });
            }

            if (!station) {
                return res.status(404).json({
                    message: 'No such station'
                });
            }
            DataModel.find({fromStation: id},function (err, datas) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting data.',
                        error: err
                    });
                }
                var data = []
                data.datas = datas;

                //return res.json({station: station, data: datas});
                return res.render('station/one_station', {station: station, data: datas});
            })});
    },

    /**
     * stationController.create()
     */
    create: function (req, res) {
        var station = new StationModel({
			address : req.body.address,
			geolocation : req.body.geolocation
        });

        station.save(function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating station',
                    error: err
                });
            }

            return res.status(201).json(station);
        });
    },

    /**
     * stationController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        StationModel.findOne({_id: id}, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station',
                    error: err
                });
            }

            if (!station) {
                return res.status(404).json({
                    message: 'No such station'
                });
            }

            station.address = req.body.address ? req.body.address : station.address;
			station.geolocation = req.body.geolocation ? req.body.geolocation : station.geolocation;
			
            station.save(function (err, station) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating station.',
                        error: err
                    });
                }

                return res.json(station);
            });
        });
    },

    /**
     * stationController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        StationModel.findByIdAndRemove(id, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the station.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },

};
