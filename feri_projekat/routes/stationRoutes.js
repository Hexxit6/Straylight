var express = require('express');
var router = express.Router();
var stationController = require('../controllers/stationController.js');

/*
 * GET
 */
router.get('/', stationController.list);
router.get('/stations', stationController.stations);
/*
 * GET
 */
router.get('/:address', stationController.show);


/*
 * POST
 */
router.post('/', stationController.create);

/*
 * PUT
 */
router.put('/:id', stationController.update);

/*
 * DELETE
 */
router.delete('/:id', stationController.remove);

module.exports = router;
