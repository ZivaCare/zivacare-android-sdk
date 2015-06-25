# zivacare-android-sdk

SDK that allows Android apps to communicate with the ZivaCare platform.
    
##How to import in your project

1.  Clone this repository
2.  Create new Android project
3.  Add the following lines i n the ***settings.gradle*** file of the new project :
    
	    include ':sdk'
		project(':sdk').setProjectDir(new File('D://path//to//sdk')) 

## How to use

***You need to have the following data ready for a ZivaCare application:***

 - clientSecret - code provided by ZivaCare application; 
 - clientId - code provided by ZivaCare application; 
 - clientUserId - usually the client e-mail address with which is registered; 
 - clientUserName - Full Name.

> - this data can be stored in a ZivaCareConfig instance or in the cache file.
	
    ZivaCareConfig config = new ZivaCareConfig(context, false);
		config.setClientSecret(clientSecret);
		config.setClientId(clientId);
		config.setClientUserId(clientUserId);
		config.setClientUserName(clientUserName);



***How to create a user to use in a ZivaCare application:***
	

> - first you need to set up a ZivaCareSDK instance:

		ZivaCareSDK ziva = new ZivaCareSDK(context, false);
		// Or 
		ZivaCareConfig config = new ZivaCareConfig(context, false);
		ZivaCareSDK ziva = new ZivaCareSDK(config);

 - context -> context of the app, used to determine where the cache file is/will be stored;
 - demo -> true = for demo, false = for real usage.
	
> - then just call the “createUser” method from your ZivaCareSDK instance, passing the needed parameters:

		ziva.createUser(clientId, clientSecret, clientUserId, clientUserName,callback);

 - clientSecret - code provided by ZivaCare application;
 - clientId - code provided by ZivaCare application;
 - clientUserId - usually the client e-mail address with which is registered;
 - clientUserName - Full Name;
 - callback - is a ZivacareCallback object that will notify you on the status of the operation.
	

> - or if the needed parameters are already in the ZivaCareConfig instance or cache, the just call the “callCreateUser” method from your ZivaCareSDK instance passing only the callback:

		ziva.createUser(callback);

***How to get the “access_token“ for an existing user to use in a ZivaCare application:***
	

> - first you need to set up a ZivaCareSDK instance, or use you existing one:

		ZivaCareSDK ziva = new ZivaCareSDK(context, false);

 - context -> the context of the app;
 - demo -> true = for demo, false = for real usage.
	
	

> - then just call the “login” method from your ZivaCareSDK instance, passing the needed parameters :

	ziva.login(clientSecret, specialToken,callback);

 - clientSecret - this is from your ZivaCareSDK instance (ziva.getConfig().getClientSecret());
 - specialToken - this is from your ZivaCareSDK instance (ziva.getConfig().getSpecialToken());
 - callback - is a ZivacareCallback object that will notify you on the status of the operation.
	

> - or if the needed parameters are already in the ZivaCareConfig instance or cache, then just call the “login” method from your ZivaCareSDK instance passing only the callback:

		ziva.login(callback);


***How to delete an existing user used in a ZivaCare application:***
	

> - first you need to set up a ZivaCareSDK instance, or use you existing one:

	ZivaCareSDK ziva = new ZivaCareSDK(context, false);

 - context -> the context of the app;
 - demo -> true = for demo, false = for real usage.
	

> - then just call the “deleteUser” method from your ZivaCareSDK instance, passing the needed parameters :

		ziva.deleteUser(clientId,callback);

 - clientId - code provided by ZivaCare application;
 - callback - is a ZivacareCallback object that will notify you on the status of the operation.

	

> - this step can be done only after the user has the “access_token”.
> 
> 
> - or if the needed parameters are already in the ZivaCareConfig instance or cache, the just call the “deleteUser” method from your ZivaCareSDK instance without the **clientId** parameter:

		ziva.deleteUser(callback);


***How to refresh the “access_token” for an existing user used in a ZivaCare application:***
	

> - first you need to set up a ZivaCareSDK instance, or use you existing one:

	ZivaCareSDK ziva = new ZivaCareSDK(context, false);

 - context -> the context of the app;
 - demo -> true = for demo, false = for real usage.

> - then just call the “refreshToken” method from your ZivaCareSDK instance, passing the needed parameters:

		ziva.refreshToken(clientId, clientSecret,callback);

 - clientId - code provided by ZivaCare application;
 - clientSecret - code provided by ZivaCare application;
 - callback - is a ZivacareCallback object that will notify you on the status of the operation.
	
> - or if the needed parameters are already in the ZivaCareConfig instance or cache, then just call the “refreshToken” method passing only the callback:

		ziva.refreshToken(callback);


***How to call a ZivaCare application endpoint:***
	

> - first you need to set up a ZivaCareSDK instance, or use you existing one:

	ZivaCareSDK ziva = new ZivaCareSDK(context, false);

 - context -> the context of the app;
 - demo -> true = for demo, false = for real usage.

> - then create a new instance of the desired endpoint passing it the ZivaCareSDK configuration:

    ZivaCareEndpoint endpoint = new ZivaCareProfileEndpoint(ziva.getConfig());

> - every endpoint has the following GET methods:

 - `getAll(int version,ZivacareCallback callback);`
 - `getByCode(int version, String code,ZivacareCallback callback);`
 - `getByDate(int version, Date date,ZivacareCallback callback);`
 - `getByPeriod(int version, Date startDate, Date endDate,ZivacareCallback callback);`
	 - version - is the endpoint version  for which to get the data (eq.: 1);
	 - code - is the endpoint filter code for which to get the data;
	 - date - is the endpoint filter date for which to get the data;
	 - startDate - is the endpoint filter start date for which to get the data;
	 - endDate - is the endpoint filter end date for which to get the data;
	 - callback - is a ZivacareCallback object that will notify you on the status of the operation.

   	

> - every endpoint has the following POST methods:

 - `setData(int version, Strign operation, String source, Object[][] dataValues,ZivacareCallback callback);`
 - `setData(int version, Strign operation, String source, String[] dataNames, Object[][] dataValues,ZivacareCallback callback);`
	 -  version - is the endpoint version  for which to put the data (eq.: 1);
	 - operation - is the endpoint operation type (ZivaCareEndpoint.OP_INSERT or ZivaCareEndpoint.OP_UPDATE);
	 - source - is the endpoint source application for which to put the data (eq.: fitbit);
	 - dataNames - not mandatory, can be null, is the endpoint parameter names for which to build the JSON, can be retrived from the endpoint instance by calling endpoint.getDataNames() method;
	 - dataValues - is the endpoint parameter values for which to build the JSON, is an array of arrays, each second level array has to be    1:1 with the dataNames , and it represent a JSON object values. It is    stacked in a first level array so that one call can register multiple data.
	 - callback - is a ZivacareCallback object that will notify you on the status of the operation.

***ZivacareCallback***

> 	- onSuccess(ZivaCareResponse response)
> 	- onError(ZivaCareResponse response)

***ZivaCareResponse***

> 	- contains the network status code (or -1 if the request failed before being requested)
> 	- contains the response string coming from the server (usually the raw JSON)

    
