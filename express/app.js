var cluster = require('cluster');
var express = require('express');
var numCPUs = require('os').cpus().length;

var tweetService = {
  counter_: 1000000,
  list: function () {
    this.counter_ += 1;
    return [
      {
        "id": this.counter_,
        "author": "author1",
        "content": "Hello, World!"
      }
    ];
  }
}

if (cluster.isMaster) {
  for (var i = 0; i < numCPUs; i++) {
    cluster.fork();
  }
  cluster.on('exit', function (worker, code, signal) {
    console.log('worker ' + worker.pid + ' died');
  });
} else {
  var app = module.exports = express();

  app.get('/v1/tweets', function (req, res) {
    res.json(tweetService.list());
  });

  app.listen(8080, function () {
    console.log('App listening on port 8080!');
  });
}
