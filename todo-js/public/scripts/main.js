requirejs.config({
    baseUrl: 'scripts',
    paths: {
        app: './app'
    }
});

require(['app/ApiClient'], function(ApiClient){
	var api = new ApiClient();
	
	api.init({baseDomain: 'http://localhost:6080', username: 'admin', password: 'password'});
	
});