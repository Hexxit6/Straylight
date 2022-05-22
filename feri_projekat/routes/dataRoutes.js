var express = require('express');
var router = express.Router();
var dataController = require('../controllers/dataController.js');

/*
 * GET
 */
router.get('/', dataController.list);

/*
 * GET
 */
router.get('/:id', dataController.show);
router.get('/all/:id', dataController.showAll);
router.get('/singlePollutant/:id/:name', dataController.getPollutantFromSingleStation);
router.get('/singlePollutant/:name', dataController.getPollutantFromAllStations);

/*
 * POST
 */
router.post('/', dataController.create);
//router.post('/getPollutants/:name', dataController.getPollutant);

/*
 * PUT
 */
router.put('/:id', dataController.update);

/*
 * DELETE
 */
router.delete('/:id', dataController.remove);

module.exports = router;
