const jwt = require("jsonwebtoken");

module.exports = function(req, res, next){
    try {
        // var token = req.cookies['jwt'];
		const authHeader = req.headers.authorization;
		const token = authHeader && authHeader.split(' ')[1];
        if (token === 'undefined') return res.status(403).send("Access denied.");
        var decoded = jwt.verify(token, process.env.TOKEN_SECRET);
		// req.userId = decoded.id;
        // req.username = decoded.username;
		req.session.userId = decoded.id;
        req.session.username = decoded.username;
        next();
    } catch (error) {
        res.status(400).send("Invalid token!");
    }
};
