var UserModel = require('../models/userModel.js');

/**
 * userController.js
 *
 * @description :: Server-side logic for managing users.
 */
module.exports = {

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

            return res.json(users);
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

            return res.json(user);
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
				req.session.userId = user._id;
				return res.redirect("/");
			}
		});
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

            return res.status(201).json(user);
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
			user.admin = req.body.admin ? req.body.admin : user.admin;
			
            user.save(function (err, user) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating user.',
                        error: err
                    });
                }

                return res.json(user);
            });
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
