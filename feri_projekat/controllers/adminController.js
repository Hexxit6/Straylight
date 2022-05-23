var UserModel = require('../models/userModel.js');

/**
 * commentController.js
 *
 * @description :: Server-side logic for managing comments.
 */
module.exports = {

    dashboard: function (req, res) {
		res.render('admin/dashboard');
    },
    
	users: function (req, res) {
        UserModel.find(function (err, users) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting user.',
                    error: err
                });
            }
            
			var data = [];
			data.users = users;
			return res.render('admin/users', data);
        });
    },

    settings: function (req, res) {
		res.render('admin/settings');
    },
}
