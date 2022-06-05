var CommentModel = require('../models/commentModel.js');
const UserModel = require("../models/userModel");

/**
 * commentController.js
 *
 * @description :: Server-side logic for managing comments.
 */
module.exports = {

    /**
     * commentController.list()
     */
    list: function (req, res) {
        var id_station = req.params.id_station;

        CommentModel.find({id_station: id_station},function (err, comments) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting comment.',
                    error: err
                });
            }

            return res.json(comments);
        });
    },

    /**
     * commentController.show()
     */
    show: function (req, res) {
        var id = req.params.id;
        var id_station = req.params.id_station;

        CommentModel.findOne({_id: id, id_station: id_station}, function (err, comment) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting comment.',
                    error: err
                });
            }

            if (!comment) {
                return res.status(404).json({
                    message: 'No such comment'
                });
            }

            return res.json(comment);
        });
    },

    /**
     * commentController.create()
     */
    create: function (req, res) {
        let name = req.body.name;
        let email = null;
        let id_user = null;
        let id_station = req.body.id_station;


        UserModel.findById(req.session.userId)
            .exec(function (error, user) {
                if (error) {
                    return next(error);
                } else {
                    if (user !== null) {
                        name = user.username;
                        email = user.email;
                        id_user = user.id;
                    }
                }


                var comment = new CommentModel({
                    content: req.body.content,
                    datetime: Date.now(),
                    name: name,
                    email: email,
                    id_user: id_user,
                    id_station: id_station
                });

                comment.save(function (err, comment) {
                    if (err) {
                        return res.status(500).json({
                            message: 'Error when creating comment',
                            error: err
                        });
                    }

                    return res.status(201).json(comment);
                });

            });


    },

    /**
     * commentController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        CommentModel.findOne({_id: id}, function (err, comment) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting comment',
                    error: err
                });
            }

            if (!comment) {
                return res.status(404).json({
                    message: 'No such comment'
                });
            }

            comment.content = req.body.content ? req.body.content : comment.content;
            comment.datetime = req.body.datetime ? req.body.datetime : comment.datetime;
            comment.name = req.body.name ? req.body.name : comment.name;
            comment.email = req.body.email ? req.body.email : comment.email;
            comment.id_user = req.body.id_user ? req.body.id_user : comment.id_user;

            comment.save(function (err, comment) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating comment.',
                        error: err
                    });
                }

                return res.json(comment);
            });
        });
    },

    /**
     * commentController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        CommentModel.findByIdAndRemove(id, function (err, comment) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the comment.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },

};
