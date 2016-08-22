requirejs.config({
    baseUrl: 'scripts',
    paths: {
        app: './app'
    }
});

require(['app/ApiClient'], function(ApiClient){
	var api = new ApiClient();
	
	api.init({user: 'admin', password: 'password'});
	
});