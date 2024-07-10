var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Bài 1 - Tạo Template HDS ' });
});
router.get('/home', function(req, res, next) {
  res.render('home', { data: 'MD18402',point:10 });
});
router.get('/chitietsp', function(req, res, next) {
  res.render('chitietsp');
});
module.exports = router;
