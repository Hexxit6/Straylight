var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
var auth = require('../middleware/auth.js');

/*
 * GET
 */
router.get('/login', userController.showLogin);
router.get('/register', userController.showRegister);
router.get('/profile', userController.profile);
// router.get('/profile', auth, userController.profile);
// router.get('/:id', userController.show);

/*
 * POST
 */
router.post('/', userController.create);
router.post('/login', userController.login);

/*
 * PUT
 */
// router.put('/:id', userController.update);

/*
 * DELETE
 */
// router.delete('/:id', userController.remove);

module.exports = router;
