var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');
var authJWT = require('../middleware/authJWT.js');
var auth = require('../middleware/auth.js');

/*
 * GET
 */
// router.get('/', userController.list);
router.get('/profile', auth.auth, userController.profile);		// needs auth
router.get('/login', userController.showLogin);
router.get('/register', userController.showRegister);
router.get('/logout', auth.auth, userController.logout);
router.get('/:id', auth.isAdmin, userController.show);
// router.get('/profile', authJWT, userController.profile);

/*
 * POST
 */
router.post('/', userController.create);
router.post('/login', userController.login);
router.post('/JWTgen', auth.auth, userController.JWTgen);

/*
 * PUT
 */
router.put('/:id', auth.auth, userController.update);	// needs auth with that user
router.put('/privileges/:id', auth.isAdmin, userController.privileges);	// needs admin

/*
 * DELETE
 */
router.delete('/:id', auth.auth, userController.remove);	// needs auth with that user or admin privs

module.exports = router;
