define(function(require) {
    var reqwest = require("../lib/reqwest");

	  var returnedModule = function () {
		
		this.init = function(config) {
			var config = config || {};

			var username = config.username;
			var password = config.password;

			var url = 'http://159.203.96.110/api/auth/token';

			reqwest({url:
				url, 
				crossOrigin: true,
				error: function (err) { 
					console.log(err);
				},
			  	success: function (resp) {
			      console.log(resp);
			    }
    		});


			
		};
	};

	return returnedModule;
});
