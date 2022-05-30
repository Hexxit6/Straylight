const jwt = require("jsonwebtoken");
var UserModel = require('../models/userModel.js');

module.exports = {
	auth: function(req, res, next){
		try {
			if(req.session && req.session.userId){
				return next();
			} else {
				var err = new Error("You must be logged in to view this page!");
				err.status = 401;
				return next(err);
			}
		} catch (error) {
			return next(error);
		}
	},

	isAdmin: function(req, res, next){
		// console.log(req.session.userId);
		try {
			if(req.session && req.session.userId){
				UserModel.findOne({_id: req.session.userId}, function(err, user){
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

					if (user.admin !== true) {
						return res.status(401).json({
							message: 'You are not authorized to view this page'
						});
					}

					return next();
				})
			} else {
				var err = new Error("You are not authorized to view this page!");
				err.status = 401;
				return next(err);
			}
		} catch (error) {
			error.status = 401;
			return next(error);
		}
	},

	jwtPass: function(req, res, next){
		try {
			const authHeader = req.headers.authorization;
			const token = authHeader && authHeader.split(' ')[1];
			if (token === 'undefined') return res.status(403).send("Access denied.");
			var decoded = jwt.verify(token, process.env.TOKEN_SECRET);
			req.session.userId = decoded.id;
			req.session.username = decoded.username;
			req.session.admin = decoded.admin;
			next();
		} catch(error) {
			next();
		}
	},
}
