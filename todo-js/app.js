var express = require('express');
var helmet = require('helmet');
var app = express();

app.use(helmet());

app.use(express.static(__dirname + '/public'));

app.listen(3000, function () {
  console.log('app listening on port 3000!');
});