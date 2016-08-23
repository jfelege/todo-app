define(function(require) {
    var reqwest = require("../lib/reqwest");

	  var returnedModule = function () {
		
		this.init = function(cfg) {
			var config = cfg || {};

			var username = config.username;
			var password = config.password;
			var baseDomain = config.baseDomain;

			var url = baseDomain + '/api/auth/token?username=' + username + '&password=' + password;

			reqwest({url:
				url,
				method: 'POST',
				type: 'json',
				crossOrigin: true,
				contentType: 'application/json',
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
