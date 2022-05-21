var express = require('express');
var router = express.Router();
var settingsController = require('../controllers/settingsController.js');

/*
 * GET
 */
router.get('/', settingsController.show);	// needs admin

/*
 * POST
 */
router.post('/start', settingsController.start);	// needs admin
router.post('/stop', settingsController.stop);	// needs admin

/*
 * PUT
 */
router.put('/:id', settingsController.update);	// needs admin

/*
 * DELETE
 */
// router.delete('/:id', settingsController.remove);	// needs admin

module.exports = router;
