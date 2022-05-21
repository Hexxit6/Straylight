var express = require('express');
var router = express.Router();
var adminController = require('../controllers/adminController.js');

/*
 * GET
 */
router.get('/', adminController.dashboard);
router.get('/users', adminController.users);
router.get('/settings', adminController.settings);
// router.get('/layout', adminController.dashboard);

module.exports = router;
