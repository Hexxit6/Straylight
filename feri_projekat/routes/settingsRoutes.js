var express = require('express');
var router = express.Router();
var settingsController = require('../controllers/settingsController.js');
var auth = require('../middleware/auth.js');

const multer = require('multer');
const storage = multer.diskStorage({
	destination: (req, file, cb) => {
		cb(null, '../principi/');
	},
	filename: (req, file, cb) => {
		cb(null, 'stations_active.txt');
	}
});
const upload = multer({storage: storage});

/*
 * POST
 */
router.post('/start', auth.isAdmin, settingsController.start);	// needs admin
router.post('/stop', auth.isAdmin, settingsController.stop);	// needs admin
router.post('/upload', auth.isAdmin, upload.single('file'), settingsController.upload);	// needs admin
router.post('/interval', auth.isAdmin, settingsController.interval);	// needs admin
router.post('/unit', auth.isAdmin, settingsController.unit);	// needs admin
router.post('/token', auth.isAdmin, settingsController.token);	// needs admin

module.exports = router;
