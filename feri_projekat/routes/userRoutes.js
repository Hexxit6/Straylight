var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
var auth = require('../middleware/auth.js');

/*
 * GET
 */
// router.get('/', userController.list);
router.get('/profile', userController.profile);		// needs auth
router.get('/login', userController.showLogin);
router.get('/register', userController.showRegister);
router.get('/logout', userController.logout);
router.get('/:id', userController.show);
// router.get('/profile', auth, userController.profile);

/*
 * POST
 */
router.post('/', userController.create);
router.post('/login', userController.login);
router.post('/JWTgen', userController.JWTgen);

/*
 * PUT
 */
router.put('/:id', userController.update);	// needs auth with that user
router.put('/privileges/:id', userController.privileges);	// needs admin

/*
 * DELETE
 */
router.delete('/:id', userController.remove);	// needs auth with that user or admin privs

module.exports = router;
