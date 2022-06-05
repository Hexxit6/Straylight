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

        DataModel.findOne({fromStation: id}).sort({"date": -1}).exec(function (err, data) {
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


    showAll: function (req, res) {
        var id = req.params.id;

        DataModel.find({fromStation: id}).sort({"date": -1}).exec(function (err, data) {
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
            pollutants :{
                AQI : req.body.pollutants.AQI,
                PM2_5 : req.body.pollutants.PM2_5,
                PM10 : req.body.pollutants.PM10,
                NO2 : req.body.pollutants.NO2,
            },
            allergens:
                {
                    Birch : req.body.allergens.Birch,
                    Alder : req.body.allergens.Alder,
                    Grasses : req.body.allergens.Grasses,
                    Ragweed : req.body.allergens.Ragweed,
                    Mugwort : req.body.allergens.Mugwort,
                    OliveTree : req.body.allergens.OliveTree,
                },
            fromStation : req.body.fromStation
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
   /* getPollutantFromSingleStation: function (req, res) {
        var id = req.params.id;
        var name = req.params.name;

        DataModel.distinct("pollutants." + name, {fromStation: id}, function (err, pollutants) {

            DataModel.distinct("allergens." + name, {fromStation: id}, function (err, allergens) {

                return res.json({pollutants: pollutants, allergens: allergens});
                /*return res.render('data/allergens_and_pollutants', {
                    pollutants: pollutants,
                    allergens: allergens,
                    name: name
                });
            })
        });
    },
    */

    getPollutantFromSingleStation: function (req, res) {
        var address = req.params.address;
        var name = req.params.name;

            DataModel.findOne({fromStation: address}, {["pollutants." + name] : 1,["allergens." + name]:1, _id:0})
                .sort({"date": -1}).exec(function (err, data) {

                //return res.json(data);
                return res.render('data/allergens_and_pollutants', {
                    data: data,
                    name: name,
                    address: address
                });
                });
    },
   /* getPollutantFromAllStations: function (req, res) {
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
            fromStation: 1,
            _id: 0
        },function (err, datas) {
            var data = [];
            data.datas = datas;


            return res.render('data/from_all_stations', {datas: datas, name:name});
            //return res.json(datas);
        });

    },

    greaterThan: function (req,res) {
        var name = (req.params.name);

        if(['allergens.'+name]) {
            DataModel.find({['allergens.'+name]: {$gt: req.query.tag}}, {['allergens.' + name]: 1,
                fromStation:1,
                _id: 0}, function (err, datas) {
                var data = [];
                data.datas = datas;
                //return res.json(datas);
                return res.render('data/greater',{datas: datas, name:name});
            });
        }else if(['pollutants.'+name]) {
            DataModel.find({['pollutants.'+name]: {$gt: req.query.tag}}, {['pollutants.' + name]: 1,
                fromStation:1,
                _id: 0},function (err, datas) {
                //return res.json(datas);
                return res.render('data/greater',{datas: datas, name:name});
            });
        }
    },
    searchStations: function (req,res) {
        var name = req.params.name;

        DataModel.aggregate([
            {$lookup: {
                    from: "stations",
                    localField: "fromStation",
                    foreignField: "address",
                    as: "stations"
                }},
            {$match : {"stations.address": req.query.tag}}
        ], function (err, datas) {
            var data =[];
            data.datas = datas;
            //return res.json(datas);
            return res.render('data/search', {datas: datas, name:name})
        });
    }
};
