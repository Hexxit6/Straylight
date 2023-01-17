var express = require('express');
var router = express.Router();
var NotificationController = require('../controllers/notificationController.js');

/*
 * GET
 */
router.get('/', NotificationController.list);

/*
 * GET
 */
router.get('/:id', NotificationController.show);

/*
 * POST
 */
router.post('/', NotificationController.create);

/*
 * PUT
 */
router.put('/:id', NotificationController.update);

/*
 * DELETE
 */
router.delete('/:id', NotificationController.remove);

module.exports = router;
