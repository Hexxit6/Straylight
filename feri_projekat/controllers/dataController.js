var DataModel = require('../models/dataModel.js');
var StationModel = require('../models/stationModel');

/**
 * dataController.js
 *
 * @description :: Server-side logic for managing datas.
 */
module.exports = {

    /**
     * dataController.list()
     */
    list: function (req, res) {
        DataModel.find(function (err, datas) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            return res.json(datas);
        });
    },

    /**
     * dataController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data.',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            return res.json(data);
        });
    },

    /**
     * dataController.create()
     */
    create: function (req, res) {
        var data = new DataModel({
			AQI : req.body.AQI,
			PM2_5 : req.body.PM2_5,
			PM10 : req.body.PM10,
			NO2 : req.body.NO2,
			Birch : req.body.Birch,
			Alder : req.body.Alder,
			Grasses : req.body.Grasses,
			Ragweed : req.body.Ragweed,
			Mugwort : req.body.Mugwort,
			OliveTree : req.body.OliveTree,
			fromStation : req.session.stationID
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

    /**
     * dataController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        DataModel.findOne({_id: id}, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting data',
                    error: err
                });
            }

            if (!data) {
                return res.status(404).json({
                    message: 'No such data'
                });
            }

            data.AQI = req.body.AQI ? req.body.AQI : data.AQI;
			data.PM2_5 = req.body.PM2_5 ? req.body.PM2_5 : data.PM2_5;
			data.PM10 = req.body.PM10 ? req.body.PM10 : data.PM10;
			data.NO2 = req.body.NO2 ? req.body.NO2 : data.NO2;
			data.Birch = req.body.Birch ? req.body.Birch : data.Birch;
			data.Alder = req.body.Alder ? req.body.Alder : data.Alder;
			data.Grasses = req.body.Grasses ? req.body.Grasses : data.Grasses;
			data.Ragweed = req.body.Ragweed ? req.body.Ragweed : data.Ragweed;
			data.Mugwort = req.body.Mugwort ? req.body.Mugwort : data.Mugwort;
			data.OliveTree = req.body.OliveTree ? req.body.OliveTree : data.OliveTree;
			data.fromStation = req.body.fromStation ? req.body.fromStation : data.fromStation;
			
            data.save(function (err, data) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating data.',
                        error: err
                    });
                }

                return res.json(data);
            });
        });
    },

    /**
     * dataController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        DataModel.findByIdAndRemove(id, function (err, data) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the data.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },
    getPollutantFromSingleStation: function (req, res) {
        var id = req.params.id;
        var name = req.params.name;

        DataModel.distinct("pollutants." + name, {fromStation: id}, function (err, pollutants) {

            DataModel.distinct("allergens." + name, {fromStation: id}, function (err, allergens) {

                //return res.json({pollutants: pollutants, allergens: allergens});
                return res.render('data/allergens_and_pollutants', {
                    pollutants: pollutants,
                    allergens: allergens,
                    name: name
                });


            })
        });
    },
    /*getPollutantFromSingleStation: function (req, res) {
        var id = req.params.id;
        var name = req.params.name;
        console.log(name);

            DataModel.distinct("pollutants."+ name , {fromStation: id}, function (err, pollutants) {

                DataModel.distinct("allergens." + name, {fromStation: id}, function (err, allergens) {

            return res.json({pollutants: pollutants, allergens: allergens});
                })});
    },
    getPollutantFromAllStations: function (req, res) {
        var name = req.params.name;

        DataModel.distinct("pollutants." + name, function (err, pollutants) {
            DataModel.distinct("allergens." + name, function (err, allergens) {
                return res.json({pollutants: pollutants, allergens: allergens});
            })
        });
    }
     */
    getPollutantFromAllStations: function (req, res) {
        var name = req.params.name;

        DataModel.find({$or: [{['allergens.'+name]: null}, {['pollutants.'+name]: null}]}, {
            ['allergens.' + name]: 1,
            ['pollutants.' + name]: 1,
            _id: 0
        }).populate('fromStation').exec(function (err, datas) {
            var data = [];
            data.datas = datas;

            return res.render('data/from_all_stations', {datas: datas, name:name});
            //return res.json(datas);
        });
    },
};
