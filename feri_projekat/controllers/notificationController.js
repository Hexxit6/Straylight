var NotificationModel = require('../models/notificationModel.js');

/**
 * notificationController.js
 *
 * @description :: Server-side logic for managing Notifications.
 */
module.exports = {

    /**
     * NotificationController.list()
     */
    list: function (req, res) {
        NotificationModel.find(function (err, Notifications) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting Notification.',
                    error: err
                });
            }

            return res.json(Notifications);
        });
    },

    /**
     * NotificationController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        NotificationModel.findOne({_id: id}, function (err, Notification) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting Notification.',
                    error: err
                });
            }

            if (!Notification) {
                return res.status(404).json({
                    message: 'No such Notification'
                });
            }

            return res.json(Notification);
        });
    },

    /**
     * NotificationController.create()
     */
    create: function (req, res) {
        var Notification = new NotificationModel({
			content : req.body.content,
			topic : req.body.topic,
			latitude : req.body.latitude,
			longitude : req.body.longitude
        });

        Notification.save(function (err, Notification) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating Notification',
                    error: err
                });
            }

            return res.status(201).json(Notification);
        });
    },

    /**
     * NotificationController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        NotificationModel.findOne({_id: id}, function (err, Notification) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting Notification',
                    error: err
                });
            }

            if (!Notification) {
                return res.status(404).json({
                    message: 'No such Notification'
                });
            }

            Notification.content = req.body.content ? req.body.content : Notification.content;
			Notification.topic = req.body.topic ? req.body.topic : Notification.topic;
			Notification.latitude = req.body.latitude ? req.body.latitude : Notification.latitude;
			Notification.longitude = req.body.longitude ? req.body.longitude : Notification.longitude;
			
            Notification.save(function (err, Notification) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating Notification.',
                        error: err
                    });
                }

                return res.json(Notification);
            });
        });
    },

    /**
     * NotificationController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        NotificationModel.findByIdAndRemove(id, function (err, Notification) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the Notification.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
