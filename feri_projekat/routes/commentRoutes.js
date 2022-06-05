var express = require('express');
var router = express.Router();
var commentController = require('../controllers/commentController.js');

/*
 * GET
 */
router.get('/:id_station', commentController.list);


/*
 * GET
 */
router.get('/:id_station/:id', commentController.show);

/*
 * POST
 */
router.post('/', commentController.create);

router.post('/delete/:id', commentController.remove);

/*
 * PUT
 */
router.put('/:id', commentController.update);

/*
 * DELETE
 */
router.delete('/:id', commentController.remove);

module.exports = router;