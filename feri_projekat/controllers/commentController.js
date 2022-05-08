var CommentModel = require('../models/commentModel.js');

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
        CommentModel.find(function (err, comments) {
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

        CommentModel.findOne({_id: id}, function (err, comment) {
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
        var comment = new CommentModel({
			content : req.body.content,
			datetime : Date.now(),
			name : req.body.name,
			email : null,
			id_user : null
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

    /**
     * QuestionController.view()
     */
    view: function (req, res) {

        commentModel.findOne({_id: req.params.id})
            .populate('id_user')
            .exec(function (err, Question) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting Question.',
                        error: err
                    });
                }

                if (!Question) {
                    return res.status(404).json({
                        message: 'No such Question'
                    });
                }
                comment.activeness++;
                comment.save(function (err, Question) {
                    if (err) {
                        return res.status(500).json({
                            message: 'Error when updating Question.',
                            error: err
                        });
                    }
                });


                comment.canApprove = req.session.userId == comment.id_user.id;

                AnswerModel.find({id_q: Question.id})
                    .populate('id_user')
                    .sort({status: 'desc', datetime: 'asc'})
                    .exec(function (err, answers) {
                        if (err) {
                            return res.status(500).json({
                                message: 'Error when getting answer.',
                                error: err
                            });
                        }

                        answers.map((answer) => answer.isMyAnswer = req.session.userId == answer.id_user.id);
                        answers.map((answer) => {
                            if((answer.upvote + answer.downvote) > 0) {
                                answer.score = answer.upvote / (answer.upvote + answer.downvote)
                            } else {
                                answer.score = 0;
                            }
                        });

                        Question.answers = answers;


                        res.render('question/view', Question);
                    });


            });
    },
};
