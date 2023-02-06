var express = require('express');
var router = express.Router();
var neuralnetController = require('../controllers/neuralnetController.js');

/*
 * GET
 */
// router.get('/', neuralnetController);

/*
 * POST
 */
router.post('/image', neuralnetController.image);
router.post('/sound', neuralnetController.sound);
router.post('/simulation', neuralnetController.simulation);

module.exports = router;
