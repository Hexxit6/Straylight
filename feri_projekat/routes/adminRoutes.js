var express = require('express');
var router = express.Router();
var adminController = require('../controllers/adminController.js');
var settingsController = require('../controllers/settingsController.js');
var auth = require('../middleware/auth.js');

/*
 * GET
 */
router.get('/', auth.isAdmin ,adminController.dashboard);
router.get('/users', auth.isAdmin, adminController.users);
router.get('/settings', auth.isAdmin, adminController.settings);
// router.get('/layout', adminController.dashboard);

/*
 * POST
 */
// router.post('/settings/start', auth.isAdmin, settingsController.start);
// router.post('/settings/stop', auth.isAdmin, settingsController.stop);


module.exports = router;
