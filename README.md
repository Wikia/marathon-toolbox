### TODO:

- Support deployment of java applications outside of docker i.e. normal marathon way
- Use marathon client library (there are some pretty good one to be found on youtube)
- get rid of Groovy i.e. translate all groovy files to Java
- Some refactoring would be nice
- Consul as a source of configuration instead of github file
- Make plugin bundle 
- On runtime check if user is running latest version of plugin. Its pretty important to watch this
- Check if we are uploading newer version to marathon so that there will be a guard against attempting to upload the same version or older of code.
- SNAPSHOT version is not properly handled


