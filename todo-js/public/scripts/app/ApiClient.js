define(function(require) {
    var request = require("../lib/request");


	  var returnedModule = function () {
		
		this.init = function(config) {
			var config = config || {};

			var username = config.username;
			var password = config.password;

			var url = 'http://159.203.96.110/api/auth/token';

			console.log(url);
			console.log(request);

			request(url, function (error, response, body) {
			  if (!error && response.statusCode == 200) {
			    console.log(body) // Show the HTML for the Google homepage.
			  }
			});
			
		};
	};

	return returnedModule;
});
