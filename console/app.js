var express = require('express');
var redis = require('redis');
var async = require('async');
var http = require('http');
var querystring = require('querystring');
var db = redis.createClient();
var app = express();
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.static(__dirname, '/public'));
app.get('/query', function(req, res){
    var list = new Array();
    var value = new Array();
    var user = new Array();
    var getList = function(key,callback){
	list.push(key);
	callback();
    };
    var getTimeline = function(key,callback){
    	db.hget(key, 'time_line', function(err,reply) {
	    value[key.split(":")[2]]=JSON.parse(reply);
	    callback();
    	});
    };
    var getTimeline_s = function(key,callback){
    };
    async.series([
	function(callback){
	    db.keys('tencent:uid:*', function (err, replies) {
		async.each(replies,getList,
			   function(err,result){
			       if (!err) {
				   callback(null, 'getList');
			       }
			   });
	    });
	},
	function(callback){
	    async.each(list,getTimeline,
		       function(err,result){
			   if (!err) {
			       callback(null, 'getTimeline');
			   }
		       });
	}
    ],function(err, results){
	if(!err){
	//console.log(results);
	// results is now equal to ['getList', 'getTimeline']
	res.render('index', {title : 'query',value : value});
	}
    });
});

app.get('/console', function(req, res){
    var list='';
    var options = {
	hostname: 'felix.torchz.net',
	port: 8080,
	path: '/system/console/bundles.json',
	auth: ':'
    };

    http.get(options, function(response) {
	//console.log('STATUS: ' + response.statusCode);
	//console.log('HEADERS: ' + JSON.stringify(response.headers));
	response.setEncoding('utf8');
	response.on('data', function (chunk){
	    list = list + chunk;
	});
	response.on('end', function (){
	    //console.log('List' + list);
	    res.render('index', {title : 'console' , bundles : JSON.parse(list)});
	});
    });

});
app.get(/^\/console\/(\w+)$/, function(req, res){
  //console.log(req.params[0]);
  //console.log(req.query.id);
    var url = '/system/console/bundles/' + req.query.id;
    console.log(url);
    var data = querystring.stringify({action: req.params[0]});
    console.log(data);
    var options = {
	hostname: 'felix.torchz.net',
	port: 8080,
	path: url,
	auth: ':',
	headers: {'Content-Type': 'application/x-www-form-urlencoded'},
	method: 'POST'
    };

    var request = http.request(options, function(response) {
	//console.log('STATUS: ' + response.statusCode);
	//console.log('HEADERS: ' + JSON.stringify(response.headers));
	response.setEncoding('utf8');
	response.on('data',function(chunk){
	    console.log('BODY: ' + chunk);
	});
    });
    request.write(data);
    request.getHeader('Content-Type');
    request.end();
    res.redirect('/console');
});
app.listen(3000);
