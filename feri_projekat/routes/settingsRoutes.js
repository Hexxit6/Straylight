var express = require('express');
var router = express.Router();
var settingsController = require('../controllers/settingsController.js');

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
router.post('/start', settingsController.start);	// needs admin
router.post('/stop', settingsController.stop);	// needs admin
router.post('/upload', upload.single('file'), settingsController.upload);	// needs admin
router.post('/interval', settingsController.interval);	// needs admin
router.post('/unit', settingsController.unit);	// needs admin
router.post('/token', settingsController.token);	// needs admin

module.exports = router;
