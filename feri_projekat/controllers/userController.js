var UserModel = require('../models/userModel.js');
var jwt = require('jsonwebtoken');

/**
 * userController.js
 *
 * @description :: Server-side logic for managing users.
 */
module.exports = {

	JWTgen: function (req, res) {
		var id = req.session.userId;
		
		UserModel.findOne({_id: id}, function (err, user) {
			if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }
            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

			var token = jwt.sign({id: user._id, username: user.username}, process.env.TOKEN_SECRET);
			return res.json({token: token});
		});
	},

    /**
     * userController.list()
     */
    list: function (req, res) {
        UserModel.find(function (err, users) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            return users;
        });
    },

	/**
	 * userController.showLogin()
	 */
	showLogin: function(req, res) {
		res.render('user/login');
	},

	/**
	 * userController.showRegister()
	 */
	showRegister: function(req, res) {
		res.render('user/register');
	},

    /**
     * userController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

            // return res.json(user);
			res.render('user/public', user);
        });
    },

	/**
	 * userController.profile()
	 */
	profile: function(req, res, next) {
		UserModel.findById(req.session.userId)
			.exec(function(error, user){
				if(error){
					return next(error);
				} else {
					if(user === null){
						var error = new Error("Not authenticated!");
						error.status = 401;
						return next(error);
					} else {
                        user.userName = req.session.userName;
                        res.render('user/profile', user);
					}
				}
			});
	},

	/**
	 * userController.login()
	 */
	login: function(req, res, next) {
		UserModel.authenticate(req.body.username, req.body.password, function(error, user){
			if(error || !user){
				var err = new Error("Wrong username or password");
				err.status = 401;
				return next(err);
			} else {
				// var token = jwt.sign({id: user._id, username: user.username}, process.env.TOKEN_SECRET);
				// res.cookie('jwt', token, {
				// 	httpOnly: false,
				// 	maxAge: 3600,
				// 	// secure: true
				// });
				req.session.userId = user._id;
                req.session.userName = user.username;
				return res.redirect("/");
			}
		});
	},

	logout: function (req, res, next) {
		if(req.session){
			req.session.destroy(function(err){
				if(err){
					return next(err);
				} else {
					return res.redirect('/');
				}
			});
		}
	},

    /**
     * userController.create()
     */
    create: function (req, res) {
        var user = new UserModel({
			username : req.body.username,
			email : req.body.email,
			password : req.body.password,
			admin : false
        });

        user.save(function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating user',
                    error: err
                });
            }

            // return res.status(201).json(user);
			return res.redirect("/user/login");
        });
    },

    /**
     * userController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

			user.username = req.body.username ? req.body.username : user.username;
			user.email = req.body.email ? req.body.email : user.email;
			user.password = req.body.password ? req.body.password : user.password;
			
			UserModel.updateOne({_id: user._id}, user, function (err, user) {
				if (err) {
					return res.status(500).json({
						message: 'Error updating user',
						error: err
					});
				}
				if (!user) {
					return res.status(404).json({
						message: 'No such user'
					});
				}
				return res.json(user);
			})
        });
    },

    privileges: function (req, res) {
        var id = req.params.id;

        UserModel.findOne({_id: id}, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user',
                    error: err
                });
            }

            if (!user) {
                return res.status(404).json({
                    message: 'No such user'
                });
            }

   //          user.username = req.body.username ? req.body.username : user.username;
			// user.email = req.body.email ? req.body.email : user.email;
			// user.password = req.body.password ? req.body.password : user.password;
			user.admin = req.body.admin ? req.body.admin : 0;

			UserModel.updateOne({_id: user._id}, user, function (err, user) {
				if (err) {
					return res.status(500).json({
						message: 'Error updating user',
						error: err
					});
				}
				if (!user) {
					return res.status(404).json({
						message: 'No such user'
					});
				}
				return res.json(user);
			})
        });
    },

    /**
     * userController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        UserModel.findByIdAndRemove(id, function (err, user) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the user.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
