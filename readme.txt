To get rid of the build path error, copy 

  ~/.m2/repository/javax/ws/rs/jsr311-api
  
to

  ~/.m2/repository/javax/ws/rs/jaxrs-api
  
Then, in the latter, rename 0.7 to 1.0 and jsr311-api-0.7.jar to 
jaxrs-api-1.0.jar.
