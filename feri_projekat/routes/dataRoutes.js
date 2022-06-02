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
router.get('/singlePollutant/:address/:name', dataController.getPollutantFromSingleStation);
router.get('/fromAllStations/:name', dataController.getPollutantFromAllStations);
router.get('/greater/:name', dataController.greaterThan);
router.get('/search/:name',dataController.searchStations);

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
