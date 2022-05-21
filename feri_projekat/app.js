var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');


var connection = require('./DatabaseUtil')
var mongoose = require('mongoose');
var mongoDB = connection;
mongoose.connect(mongoDB, {
  useNewUrlParser: true,
  useUnifiedTopology: true
});
mongoose.Promise = global.Promise;
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));


/*
const mongoose = require('mongoose')

const url = `mongodb+srv://andjela:1234@projekat.skzv5.mongodb.net/projekat?retryWrites=true&w=majority`;

const connectionParams={
  useNewUrlParser: true,
  //useCreateIndex: true,
  useUnifiedTopology: true
}
mongoose.connect(url,connectionParams)
    .then( () => {
      console.log('Connected to the database ')
    })
    .catch( (err) => {
      console.error(`Error connecting to the database. n${err}`);
    })
*/

var indexRouter = require('./routes/index');
//var usersRouter = require('./routes/users');
var stationRouter = require('./routes/stationRoutes');
var dataRouter = require('./routes/dataRoutes');
var commentRouter = require('./routes/commentRoutes');

var app = express();

var hbs = require('hbs');

// register path to partials
hbs.registerPartials(__dirname + '/views/partials');

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hbs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
//app.use('/users', usersRouter);
app.use('/stations', stationRouter);
app.use('/data', dataRouter);
app.use('/comment', commentRouter);


var session = require('express-session');
var MongoStore = require('connect-mongo');
// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
