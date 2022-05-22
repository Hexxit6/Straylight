var express = require('express');
var router = express.Router();
var adminController = require('../controllers/adminController.js');
var settingsController = require('../controllers/settingsController.js');

/*
 * GET
 */
router.get('/', adminController.dashboard);
router.get('/users', adminController.users);
router.get('/settings', adminController.settings);
// router.get('/layout', adminController.dashboard);

/*
 * POST
 */
router.post('/settings/start', settingsController.start);
router.post('/settings/stop', settingsController.stop);


module.exports = router;
